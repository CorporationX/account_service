package faang.school.accountservice.service.payment;

import faang.school.accountservice.dto.balance.BalanceUpdateDto;
import faang.school.accountservice.exception.NotFoundException;
import faang.school.accountservice.model.Balance;
import faang.school.accountservice.model.BalanceAudit;
import faang.school.accountservice.model.Payment;
import faang.school.accountservice.model.enums.PaymentStatus;
import faang.school.accountservice.repository.BalanceAuditRepository;
import faang.school.accountservice.repository.BalanceRepository;
import faang.school.accountservice.repository.PaymentRepository;
import faang.school.accountservice.service.balance_audit.BalanceAuditService;
import faang.school.accountservice.validator.payment.PaymentValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final BalanceRepository balanceRepository;
    private final PaymentValidator paymentValidator;
    private final BalanceAuditService balanceAuditService;
    private final BalanceAuditRepository balanceAuditRepository;

    @Transactional
    public void authorizePayment(Long userId, Long paymentId) {
        Payment payment = getPaymentById(paymentId);
        try {
            Balance senderBalance = getBalanceByAccountNumber(payment.getSenderAccountNumber(), "Sender");
            Balance receiverBalance = getBalanceByAccountNumber(payment.getReceiverAccountNumber(), "Receiver");

            paymentValidator.validateAuthorization(senderBalance, payment, userId);
            updateBalances(senderBalance, receiverBalance, payment.getAmount());

            updatePaymentStatus(payment, PaymentStatus.READY_TO_CLEAR);
            logBalancesAudit(senderBalance, receiverBalance, paymentId);

            log.info("Successfully authorized payment with ID {} and authorized amount {} to account {}",
                    paymentId, payment.getAmount(), payment.getReceiverAccountNumber());
        } catch (Exception e) {
            log.error("Error occurred while authorizing payment: {}", e.getMessage());
            saveFailedPayment(payment);
            deleteAudit(payment);
        }
    }

    @Transactional
    public void cancelPayment(Long userId, Long paymentId) {
        Payment payment = getPaymentById(paymentId);

        Balance senderBalance = getBalanceByAccountNumber(payment.getSenderAccountNumber(), "Sender");
        Balance receiverBalance = getBalanceByAccountNumber(payment.getReceiverAccountNumber(), "Receiver");

        paymentValidator.validateCancelPayment(userId, payment, senderBalance);

        updateBalancesForCancellation(senderBalance, receiverBalance, payment.getAmount());
        updatePaymentStatus(payment, PaymentStatus.CANCELED);
        logBalancesAudit(senderBalance, receiverBalance, paymentId);

        log.info("Successfully canceled payment with ID {}", paymentId);
    }

    @Transactional
    public void clearPayment(Long paymentId) {
        Payment payment = getPaymentById(paymentId);
        try {
            Balance senderBalance = getBalanceByAccountNumber(payment.getSenderAccountNumber(), "Sender");
            Balance receiverBalance = getBalanceByAccountNumber(payment.getReceiverAccountNumber(), "Receiver");

            paymentValidator.validateClearing(senderBalance, payment);
            updateBalancesForClearing(senderBalance, receiverBalance, payment.getAmount());
            updatePaymentStatus(payment, PaymentStatus.CLEAR);
            logBalancesAudit(senderBalance, receiverBalance, paymentId);

            log.info("Successfully cleared payment with ID {} and credited amount {} to account {}",
                    paymentId, payment.getAmount(), payment.getReceiverAccountNumber());
        } catch (Exception e) {
            log.error("Error occurred while clearing payment: {}", e.getMessage());
            saveFailedPayment(payment);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveFailedPayment(Payment payment) {
        payment.setPaymentStatus(PaymentStatus.FAILURE);
        paymentRepository.save(payment);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteAudit(Payment payment) {
        List<BalanceAudit> audits = balanceAuditRepository.findByPaymentNumber(payment.getId());
        if (!audits.isEmpty()) {
            balanceAuditRepository.deleteAll(audits);
        }
    }

    private Payment getPaymentById(Long paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NotFoundException("Payment not found"));
    }

    private Balance getBalanceByAccountNumber(String accountNumber, String accountType) {
        return balanceRepository.findBalanceByAccountNumber(accountNumber)
                .orElseThrow(() -> new NotFoundException(accountType + " balance hasn't been found"));
    }

    private void updateBalances(Balance senderBalance, Balance receiverBalance, BigDecimal amount) {
        senderBalance.setAuthorizationBalance(senderBalance.getAuthorizationBalance().subtract(amount));
        receiverBalance.setAuthorizationBalance(receiverBalance.getAuthorizationBalance().add(amount));
        balanceRepository.save(senderBalance);
        balanceRepository.save(receiverBalance);
    }

    private void updateBalancesForCancellation(Balance senderBalance, Balance receiverBalance, BigDecimal amount) {
        senderBalance.setAuthorizationBalance(senderBalance.getAuthorizationBalance().add(amount));
        receiverBalance.setAuthorizationBalance(receiverBalance.getAuthorizationBalance().subtract(amount));
        balanceRepository.save(senderBalance);
        balanceRepository.save(receiverBalance);
    }

    private void updateBalancesForClearing(Balance senderBalance, Balance receiverBalance, BigDecimal amount) {
        senderBalance.setActualBalance(senderBalance.getActualBalance().subtract(amount));
        receiverBalance.setActualBalance(receiverBalance.getActualBalance().add(amount));
        balanceRepository.save(senderBalance);
        balanceRepository.save(receiverBalance);
    }

    private void updatePaymentStatus(Payment payment, PaymentStatus status) {
        payment.setPaymentStatus(status);
        paymentRepository.save(payment);
    }

    private void logBalancesAudit(Balance senderBalance, Balance receiverBalance, Long paymentId) {
        createBalanceAudit(senderBalance, paymentId);
        createBalanceAudit(receiverBalance, paymentId);
    }

    private void createBalanceAudit(Balance balance, Long paymentId) {
        BalanceUpdateDto balanceAudit = BalanceUpdateDto.builder()
                .accountId(balance.getAccount().getId())
                .authorizedBalance(balance.getAuthorizationBalance().longValue())
                .actualBalance(balance.getActualBalance().longValue())
                .paymentNumber(paymentId)
                .build();
        balanceAuditService.createNewAudit(balanceAudit);
    }
}