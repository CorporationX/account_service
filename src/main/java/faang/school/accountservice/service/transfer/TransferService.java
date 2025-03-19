package faang.school.accountservice.service.transfer;

import faang.school.accountservice.dto.Money;
import faang.school.accountservice.dto.transfer_request.TransferRequestDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.AuthPayment;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.entity.TransferRequest;
import faang.school.accountservice.enums.transfer_request.TransferStatus;
import faang.school.accountservice.exception.ValidationException;
import faang.school.accountservice.exception.non_retryable.CurrencyMismatchException;
import faang.school.accountservice.exception.non_retryable.EntityNotFoundException;
import faang.school.accountservice.repository.TransferRepository;
import faang.school.accountservice.service.AccountService;
import faang.school.accountservice.service.balance.BalanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class TransferService {
    private final TransferRepository transferRepository;
    private final BalanceService balanceService;
    private final AccountService accountService;

    @Transactional
    public TransferRequest getById(UUID id) {
        return transferRepository.findByIdOrThrow(id);
    }

    @Transactional(readOnly = true)
    public boolean checkPaymentForDuplicates(UUID transferId) {
        boolean existsById = transferRepository.existsByIdOrThrow(transferId);
        if (existsById) {
            log.error("Duplicate transfer request with id {}", transferId);
            return true;
        }
        return false;
    }

    @Transactional
    public void authorizeTransfer(TransferRequestDto dto)
            throws CurrencyMismatchException, ValidationException, EntityNotFoundException {

        TransferRequest transferRequest = createFromDto(dto);
        Account senderAccount = transferRequest.getSenderAccount();

        Balance senderBalance = senderAccount.getBalance();

        AuthPayment authPayment = balanceService.freezeFundsForTransfer(
                senderBalance.getId(), new Money(transferRequest.getAmount(), transferRequest.getCurrency()));

        transferRequest.setAuthPayment(authPayment);
        transferRequest.setStatus(TransferStatus.AUTHORIZED);
        transferRequest.setKafkaPublished(false);
        transferRepository.save(transferRequest);

        log.info("TransferRequest {} created, money frozen at account id {}", transferRequest.getId(), transferRequest.getSenderAccount().getId());
    }


    @Transactional
    public void cancelPayment(TransferRequest payment) {
        Account senderAccount = accountService.getAccountByNumber(payment.getSenderAccount().getAccountNumber());
        balanceService.unfreezeFundsForTransfer(
                senderAccount.getBalance().getId(), payment.getAuthPayment().getId());
        payment.setStatus(TransferStatus.CANCELLED);
        payment.setKafkaPublished(false);

        transferRepository.save(payment);
    }

    @Transactional
    public void transferFunds(TransferRequest payment) {
        Account senderAccount = accountService.getAccountByNumber(payment.getSenderAccount().getAccountNumber());
        Account receiverAccount = accountService.getAccountByNumber(payment.getReceiverAccount().getAccountNumber());

        balanceService.finalizeTransfer(senderAccount.getBalance().getId(), payment.getAuthPayment().getId());
        balanceService.increaseCurrentBalancePessimistic(receiverAccount.getBalance().getId(), payment.getAmount());

        payment.setStatus(TransferStatus.CLEARED);
        payment.setKafkaPublished(false);
        transferRepository.save(payment);
    }

    private TransferRequest createFromDto(TransferRequestDto dto) {
        Account senderAccount = accountService.getAccountByNumber(dto.senderAccountNumber());
        Account receiverAccount = accountService.getAccountByNumber(dto.receiverAccountNumber());

        return TransferRequest.builder()
                .id(dto.id())
                .senderAccount(senderAccount)
                .receiverAccount(receiverAccount)
                .amount(dto.amount())
                .currency(dto.currency())
                .paymentType(dto.paymentType())
                .build();
    }
}
