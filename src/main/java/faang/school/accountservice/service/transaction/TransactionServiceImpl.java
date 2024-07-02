package faang.school.accountservice.service.transaction;

import faang.school.accountservice.dto.transaction.TransactionDto;
import faang.school.accountservice.dto.transaction.TransactionDtoToCreate;
import faang.school.accountservice.enums.TransactionStatus;
import faang.school.accountservice.exception.NotEnoughMoneyException;
import faang.school.accountservice.exception.NotFoundException;
import faang.school.accountservice.model.Balance;
import faang.school.accountservice.model.Transaction;
import faang.school.accountservice.repository.BalanceRepository;
import faang.school.accountservice.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Service
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final BalanceRepository balanceRepository;
    private final TransactionRepository transactionRepository;

    @Override
    @Transactional
    public void createTransaction(Long userId, TransactionDtoToCreate dto){
        Balance senderBalance = balanceRepository.findBalanceByAccountNumber(dto.getSenderAccountNumber())
                .orElseThrow(() -> new NotFoundException("Sender balance hasn't found"));
        Balance receiverBalance = balanceRepository.findBalanceByAccountNumber(dto.getSenderAccountNumber())
                .orElseThrow(() -> new NotFoundException("Receiver balance hasn't found"));

        if (senderBalance.getActualBalance().compareTo(dto.getAmount()) < 0) {
            throw new NotEnoughMoneyException("Not enough money");
        }

        validateIsAccountNumberOwner(userId);

        Transaction transaction = new Transaction();
        transaction.setTransactionStatus(TransactionStatus.NEW);
        transactionRepository.save(transaction);

        BigDecimal authorizedSenderBalance = senderBalance.getActualBalance().subtract(dto.getAmount());
        senderBalance.setAuthorizationBalance(authorizedSenderBalance);
        balanceRepository.save(senderBalance);

        RedisPaymentDto redisPaymentDto = new RedisPaymentDto(userId, senderBalance.getId(), receiverBalance.getId(),
                deposit, createPaymentRequest.currency(), PaymentStatus.PENDING);
        transactionPublisher.publish(redisPaymentDto);
    }

    @Override
    public TransactionDto getTransaction(long id) {
        return null;
    }

    @Override
    public void clearTransaction(TransactionDto dto) {

    }

    @Override
    @Transactional
    public void cancelTransaction(long id){
//        Balance senderBalance = balanceRepository.findById(redisPaymentDto.senderBalanceNumber())
//                .orElseThrow(() -> new EntityNotFoundException("Sender balance hasn't found"));
//        Balance getterBalance = balanceRepository.findById(redisPaymentDto.senderBalanceNumber())
//                .orElseThrow(() -> new EntityNotFoundException("Getter balance hasn't found"));
//        BigDecimal updatedBalance = senderBalance.getAuthorizationBalance().add(senderBalance.getCurrentBalance());
//        senderBalance.setCurrentBalance(updatedBalance);
//        balanceRepository.save(senderBalance);
//
//        String lock = String.valueOf(senderBalance.getId() + senderBalance.getId());
//        Request request = requestService.getRequestByUserIdAndLock(redisPaymentDto.userId(), lock);
//        request.setRequestStatus(RequestStatus.REJECTED);
//        request.setIsOpenRequest(false);
//        request.setRequestType(RequestType.CLOSE);
//        requestService.saveRequestInternal(request);
    }
}