package faang.school.accountservice.service.transaction;

import faang.school.accountservice.dto.transaction.TransactionDto;
import faang.school.accountservice.dto.transaction.TransactionDtoToCreate;
import faang.school.accountservice.enums.TransactionStatus;
import faang.school.accountservice.event.NewTransactionEvent;
import faang.school.accountservice.exception.NotEnoughMoneyException;
import faang.school.accountservice.exception.NotFoundException;
import faang.school.accountservice.mapper.TransactionMapper;
import faang.school.accountservice.model.Balance;
import faang.school.accountservice.model.Transaction;
import faang.school.accountservice.publisher.NewTransactionPublisher;
import faang.school.accountservice.repository.BalanceRepository;
import faang.school.accountservice.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class TransactionServiceImpl implements TransactionService {

    private final BalanceRepository balanceRepository;
    private final TransactionRepository transactionRepository;
    private final NewTransactionPublisher newTransactionPublisher;
    private final TransactionMapper transactionMapper;

    @Override
    @Transactional
    public TransactionDto createTransaction(Long userId, TransactionDtoToCreate dto) {
        Balance senderBalance = balanceRepository.findBalanceByAccountNumber(dto.getSenderAccountNumber())
                .orElseThrow(() -> new NotFoundException("Sender balance hasn't been found"));
        Balance receiverBalance = balanceRepository.findBalanceByAccountNumber(dto.getReceiverAccountNumber())
                .orElseThrow(() -> new NotFoundException("Receiver balance hasn't been found"));

        if (senderBalance.getAuthorizationBalance().compareTo(dto.getAmount()) < 0) {
            throw new NotEnoughMoneyException("Not enough money");
        }

        BigDecimal newAuthorizedSenderBalance = senderBalance.getAuthorizationBalance().subtract(dto.getAmount());
        senderBalance.setAuthorizationBalance(newAuthorizedSenderBalance);
        balanceRepository.save(senderBalance);

        Transaction transaction = new Transaction();
        transaction.setIdempotencyKey(dto.getIdempotencyKey());
        transaction.setSenderAccountNumber(dto.getSenderAccountNumber());
        transaction.setReceiverAccountNumber(dto.getReceiverAccountNumber());
        transaction.setCurrency(dto.getCurrency());
        transaction.setAmount(dto.getAmount());
        transaction.setTransactionStatus(TransactionStatus.NEW);
        transaction.setScheduledAt(LocalDateTime.Next.Saturday);
        transaction.setCreatedAt(LocalDateTime.now());

        transactionRepository.save(transaction);

        NewTransactionEvent event = new NewTransactionEvent(
                senderBalance.getId(),
                receiverBalance.getId(),
                dto.getCurrency(),
                dto.getAmount()
        );

        newTransactionPublisher.publish(event);
        return transactionMapper.toDto(transaction);
    }

    @Override
    @Transactional
    public TransactionDto cancelTransaction(Long userId, Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Transaction hasn't been found"));

        Balance senderBalance = balanceRepository.findBalanceByAccountNumber(transaction.getSenderAccountNumber())
                .orElseThrow(() -> new NotFoundException("Sender balance hasn't been found"));

        BigDecimal updatedBalance = senderBalance.getAuthorizationBalance().add(transaction.getAmount());
        senderBalance.setAuthorizationBalance(updatedBalance);
        balanceRepository.save(senderBalance);

        transaction.setTransactionStatus(TransactionStatus.CANCELLED);
        transactionRepository.save(transaction);
    }

    @Override
    public TransactionDto getTransaction(Long id) {
        return transactionMapper.toDto(transactionRepository.findById(id).orElseThrow(() -> new NotFoundException(
                String.format("Not found transaction with id %d", id))));
    }

//    @Override
//    @Transactional
//    public void clearTransaction(TransactionDto dto) {
//        Transaction transaction = transactionRepository.findById(dto.getId())
//                .orElseThrow(() -> new NotFoundException("Transaction hasn't been found"));
//
//        Balance senderBalance = balanceRepository.findBalanceByAccountNumber(transaction.getSenderAccountNumber())
//                .orElseThrow(() -> new NotFoundException("Sender balance hasn't been found"));
//        Balance receiverBalance = balanceRepository.findBalanceByAccountNumber(transaction.getReceiverAccountNumber())
//                .orElseThrow(() -> new NotFoundException("Receiver balance hasn't been found"));
//
//
//        BigDecimal newActualSenderBalance = senderBalance.getActualBalance().subtract(dto.getAmount());
//        senderBalance.setActualBalance(newActualSenderBalance);
//
//        BigDecimal newActualReceiverBalance = receiverBalance.getActualBalance().add(dto.getAmount());
//        receiverBalance.setActualBalance(newActualReceiverBalance);
//
//        balanceRepository.save(senderBalance);
//        balanceRepository.save(receiverBalance);
//
//        transaction.setTransactionStatus(TransactionStatus.CLEAR);
//        transactionRepository.save(transaction);
//    }
}
