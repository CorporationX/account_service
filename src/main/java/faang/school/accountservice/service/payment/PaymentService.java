package faang.school.accountservice.service.payment;

import faang.school.accountservice.annotation.PublishPayment;
import faang.school.accountservice.dto.payment.request.PaymentRequest;
import faang.school.accountservice.dto.payment.request.CancelPaymentRequest;
import faang.school.accountservice.dto.payment.request.ClearingPaymentRequest;
import faang.school.accountservice.dto.payment.request.ErrorPaymentRequest;
import faang.school.accountservice.dto.payment.response.CancelPaymentResponse;
import faang.school.accountservice.dto.payment.response.ClearingPaymentResponse;
import faang.school.accountservice.dto.payment.response.ErrorPaymentResponse;
import faang.school.accountservice.dto.payment.response.PaymentResponse;
import faang.school.accountservice.exception.AccountNotFoundException;
import faang.school.accountservice.exception.balance.BalanceHasBeenUpdatedException;
import faang.school.accountservice.exception.payment.PaymentHasBeenUpdatedException;
import faang.school.accountservice.model.balance.Balance;
import faang.school.accountservice.model.payment.Payment;
import faang.school.accountservice.repository.PaymentRepository;
import faang.school.accountservice.repository.BalanceRepository;
import faang.school.accountservice.validator.payment.PaymentValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static faang.school.accountservice.model.payment.PaymentStatus.CLOSED;
import static faang.school.accountservice.model.payment.PaymentStatus.REJECTED;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BalanceRepository balanceRepository;
    private final PaymentValidator paymentValidator;

    @PublishPayment(returnedType = PaymentResponse.class)
    @Transactional
    public Payment authorizePayment(PaymentRequest request) {
        log.info("Authorize payment, operationId={}", request.getOperationId());
        Balance source = findBalanceById(request.getSourceAccountId());
        Balance target = findBalanceById(request.getTargetAccountId());
        paymentValidator.checkFreeAmount(request.getOperationId(), source, request.getAmount());
        adjustSourceBalanceForAuthorization(source, request.getAmount());
        Payment payment = Payment.builder()
                .amount(request.getAmount())
                .category(request.getCategory())
                .sourceBalance(source)
                .targetBalance(target)
                .build();
        saveBalance(source);
        Payment savedPayment = savePayment(payment);
        log.info("Payment authorized, paymentId={}", savedPayment.getId());
        return savedPayment;
    }

    @PublishPayment(returnedType = ClearingPaymentResponse.class)
    @Transactional
    public Payment clearingPayment(ClearingPaymentRequest request) {
        log.info("Clearing payment, operationId={}", request.getOperationId());
        Payment payment = findPaymentById(request.getOperationId());
        paymentValidator.checkAuthPaymentStatus(payment);
        Balance source = payment.getSourceBalance();
        Balance target = payment.getTargetBalance();
        clearSourceBalance(source, payment.getAmount());
        increaseTargetBalance(target, payment.getAmount());
        payment.setStatus(CLOSED);
        saveBalance(source);
        saveBalance(target);
        Payment savedPayment = savePayment(payment);
        log.info("Payment cleared, paymentId={}", savedPayment.getId());
        return savedPayment;
    }

    //TODO добавить аудит после трансакций
    @PublishPayment(returnedType = CancelPaymentResponse.class)
    @Transactional
    public Payment cancelPayment(CancelPaymentRequest request) {
        log.info("Cancel payment, operationId={}", request.getOperationId());
        Payment payment = rejectPayment(request.getOperationId());
        log.info("Payment cancelled, paymentId={}", payment.getId());
        return payment;
    }

    @PublishPayment(returnedType = ErrorPaymentResponse.class)
    @Transactional
    public Payment errorPayment(ErrorPaymentRequest request) {
        log.info("Error payment, operationId={}", request.getOperationId());
        Payment payment = rejectPayment(request.getOperationId());
        log.info("Payment errored, paymentId={}", payment.getId());
        return payment;
    }

    private Payment rejectPayment(UUID paymentId) {
        Payment payment = findPaymentById(paymentId);
        paymentValidator.checkAuthPaymentStatus(payment);
        Balance source = payment.getSourceBalance();
        revertSourceBalance(source, payment.getAmount());
        payment.setStatus(REJECTED);
        saveBalance(source);
        return savePayment(payment);
    }

    private Payment findPaymentById(UUID id) {
        return paymentRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Payment with ID %s not found".formatted(id))
        );
    }

    private Balance findBalanceById(Long accountId) {
        return balanceRepository.findByAccountId(accountId)
                .orElseThrow(() -> {
                    log.warn("Balance not found for accountId={}", accountId);
                    return new AccountNotFoundException("Account with id=%d not found".formatted(accountId));
                });
    }

    private void adjustSourceBalanceForAuthorization(Balance balance, BigDecimal amount) {
        balance.setAuthorizationBalance(balance.getAuthorizationBalance().add(amount));
        balance.setActualBalance(balance.getActualBalance().subtract(amount));
    }

    private void clearSourceBalance(Balance balance, BigDecimal amount) {
        balance.setAuthorizationBalance(balance.getAuthorizationBalance().subtract(amount));
    }

    private void increaseTargetBalance(Balance balance, BigDecimal amount) {
        balance.setActualBalance(balance.getActualBalance().add(amount));
    }

    private void revertSourceBalance(Balance balance, BigDecimal amount) {
        balance.setAuthorizationBalance(balance.getAuthorizationBalance().subtract(amount));
        balance.setActualBalance(balance.getActualBalance().add(amount));
    }

    private void saveBalance(Balance balance) {
        try {
            balanceRepository.saveAndFlush(balance);
        } catch (OptimisticLockingFailureException ex) {
            throw new BalanceHasBeenUpdatedException("Balance with id=%s has been updated. Reload information."
                    .formatted(balance.getId()));
        }
    }

    private Payment savePayment(Payment payment) {
        try {
            return paymentRepository.saveAndFlush(payment);
        } catch (OptimisticLockingFailureException ex) {
            throw new PaymentHasBeenUpdatedException("Payment with id=%s has been updated. Reload information."
                    .formatted(payment.getId()));
        }
    }
}
