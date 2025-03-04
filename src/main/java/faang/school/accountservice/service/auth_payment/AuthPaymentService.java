package faang.school.accountservice.service.auth_payment;

import faang.school.accountservice.dto.auth_payment.PaymentResponse;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.AuthPayment;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.enums.auth_payment.AuthPaymentStatus;
import faang.school.accountservice.enums.auth_payment.PaymentProcessingResult;
import faang.school.accountservice.exception.non_retryable.EntityNotFoundException;
import faang.school.accountservice.exception.non_retryable.NotEnoughFundsException;
import faang.school.accountservice.repository.AuthPaymentRepository;
import faang.school.accountservice.service.AccountService;
import faang.school.accountservice.service.BalanceService;
import faang.school.accountservice.service.OutboxEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthPaymentService {
    private final AuthPaymentRepository authPaymentRepository;
    private final OutboxEventService outboxEventService;
    private final BalanceService balanceService;
    private final AccountService accountService;

    @Transactional
    public AuthPayment save(AuthPayment paymentRequest) {
        return authPaymentRepository.save(paymentRequest);
    }

    @Transactional
    public AuthPayment getById(UUID id) {
        return authPaymentRepository.findByIdOrThrow(id);
    }

    public void processPayment(AuthPayment payment, String responseTopic, Runnable action) {
        try {
            action.run();
        } catch (NotEnoughFundsException e) {
            log.error("Not enough funds for payment {}", payment.getId());
            outboxEventService.save(payment.getId(), PaymentResponse.class, responseTopic,
                    new PaymentResponse(payment.getId(), PaymentProcessingResult.ERROR));
        }
    }

    @Transactional
    public void authorizePaymentRequest(AuthPayment paymentRequest, String kafkaTopic) throws NotEnoughFundsException {
        balanceService.freezeMoneyAtBalance(
                paymentRequest.getSenderAccount().getBalance(), paymentRequest.getAmount());
        paymentRequest.setStatus(AuthPaymentStatus.AUTHORIZED);
        AuthPayment saved = authPaymentRepository.save(paymentRequest);
        outboxEventService.save(saved.getId(), PaymentResponse.class, kafkaTopic,
                new PaymentResponse(saved.getId(), PaymentProcessingResult.AUTHORIZED));
    }

    public AuthPayment fetchPaymentWithAccounts(AuthPayment payment, Long senderAccountId, Long receiverAccountId) throws EntityNotFoundException {
        Account senderAccount = accountService.getAccountById(senderAccountId);
        Account receiverAccount = accountService.getAccountById(receiverAccountId);
        payment.setSenderAccount(senderAccount);
        payment.setReceiverAccount(receiverAccount);
        return payment;
    }

    @Transactional(readOnly = true)
    public void checkPaymentForDuplicates(UUID paymentId) {
        boolean existsById = authPaymentRepository.existsById(paymentId);
        if (existsById) {
            log.error("Duplicate AuthPayment with id {}", paymentId);
        }
    }

    @Transactional
    public AuthPayment transferFunds(AuthPayment payment, String kafkaTopic) throws NotEnoughFundsException {
        Balance senderBalance = balanceService.findBalanceByAccountIdOrThrow(
                payment.getSenderAccount()
                        .getId());
        Balance receiverBalance = balanceService.findBalanceByAccountIdOrThrow(
                payment.getReceiverAccount()
                        .getId());

        balanceService.writeOffFromAuthBalance(senderBalance, payment.getAmount());
        balanceService.increaseFromAnotherAuthBalance(receiverBalance, payment.getAmount());

        payment.setStatus(AuthPaymentStatus.CLEARED);
        AuthPayment saved = authPaymentRepository.save(payment);
        PaymentResponse response = new PaymentResponse(saved.getId(), PaymentProcessingResult.CLEARED);
        outboxEventService.save(saved.getId(), PaymentResponse.class, kafkaTopic, response);

        return payment;
    }

    @Transactional
    public void cancelPayment(AuthPayment payment, String kafkaTopic) {
        payment.setStatus(AuthPaymentStatus.CANCELLED);
        AuthPayment saved = authPaymentRepository.save(payment);
        balanceService.unfreezeMoney(payment.getSenderAccount().getBalance(), payment.getAmount());

        outboxEventService.save(
                saved.getId(),
                PaymentResponse.class,
                kafkaTopic,
                new PaymentResponse(payment.getId(), PaymentProcessingResult.CANCELLED));
    }
}
