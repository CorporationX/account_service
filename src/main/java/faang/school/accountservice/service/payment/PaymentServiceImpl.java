package faang.school.accountservice.service.payment;

import faang.school.accountservice.exception.NotFoundException;
import faang.school.accountservice.model.Balance;
import faang.school.accountservice.model.Payment;
import faang.school.accountservice.model.enums.PaymentStatus;
import faang.school.accountservice.repository.BalanceRepository;
import faang.school.accountservice.repository.PaymentRepository;
import faang.school.accountservice.validator.payment.PaymentValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final BalanceRepository balanceRepository;
    private final PaymentValidator paymentValidator;

    @Transactional
    public void authorizePayment(Long userId, Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NotFoundException("Payment not found"));

        try {
            Balance senderBalance = balanceRepository.findBalanceByAccountNumber(payment.getSenderAccountNumber())
                    .orElseThrow(() -> new NotFoundException("Sender balance hasn't been found"));
            Balance receiverBalance = balanceRepository.findBalanceByAccountNumber(payment.getReceiverAccountNumber())
                    .orElseThrow(() -> new NotFoundException("Receiver balance hasn't been found"));

            paymentValidator.validateUserIsBalanceOwner(senderBalance.getId(), userId);
            paymentValidator.validateSenderHaveEnoughMoneyOnBalance(senderBalance, payment);
            paymentValidator.validateStatus(payment, PaymentStatus.NEW);

            senderBalance.setAuthorizationBalance(senderBalance.getAuthorizationBalance().subtract(payment.getAmount()));
            receiverBalance.setAuthorizationBalance(receiverBalance.getAuthorizationBalance().add(payment.getAmount()));

            balanceRepository.save(senderBalance);
            balanceRepository.save(receiverBalance);

            payment.setPaymentStatus(PaymentStatus.READY_TO_CLEAR);
            paymentRepository.save(payment);

        } catch (Exception e) {
            log.error("Error occurred while authorizing payment: ", e);
            saveFailedPayment(payment);
            throw e;
        }
    }

    @Transactional
    public void cancelPayment(Long userId, Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NotFoundException("Payment not found"));

        Balance senderBalance = balanceRepository.findBalanceByAccountNumber(payment.getSenderAccountNumber())
                .orElseThrow(() -> new NotFoundException("Sender balance hasn't been found"));
        Balance receiverBalance = balanceRepository.findBalanceByAccountNumber(payment.getReceiverAccountNumber())
                .orElseThrow(() -> new NotFoundException("Receiver balance hasn't been found"));

        paymentValidator.validatePaymentStatusForCancel(payment);
        paymentValidator.validateUserIsBalanceOwner(senderBalance.getId(), userId);

        senderBalance.setAuthorizationBalance(senderBalance.getAuthorizationBalance().add(payment.getAmount()));
        receiverBalance.setAuthorizationBalance(receiverBalance.getAuthorizationBalance().subtract(payment.getAmount()));

        balanceRepository.save(senderBalance);
        balanceRepository.save(receiverBalance);

        payment.setPaymentStatus(PaymentStatus.CANCELED);
        paymentRepository.save(payment);
    }

    @Transactional
    public void clearPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NotFoundException("Payment not found"));

        try {
            Balance senderBalance = balanceRepository.findBalanceByAccountNumber(payment.getSenderAccountNumber())
                    .orElseThrow(() -> new NotFoundException("Sender balance hasn't been found"));
            Balance receiverBalance = balanceRepository.findBalanceByAccountNumber(payment.getReceiverAccountNumber())
                    .orElseThrow(() -> new NotFoundException("Receiver balance hasn't been found"));

            paymentValidator.validateStatus(payment, PaymentStatus.READY_TO_CLEAR);

            senderBalance.setActualBalance(senderBalance.getActualBalance().subtract(payment.getAmount()));
            receiverBalance.setActualBalance(receiverBalance.getActualBalance().add(payment.getAmount()));
            balanceRepository.save(senderBalance);
            balanceRepository.save(receiverBalance);

            payment.setPaymentStatus(PaymentStatus.CLEAR);
            paymentRepository.save(payment);

            log.info("Successfully cleared payment with ID {} and credited amount {} to account {}",
                    paymentId, payment.getAmount(), payment.getReceiverAccountNumber());
        } catch (Exception e) {
            log.error("Error occurred while clearing payment: ", e);
            saveFailedPayment(payment);
            throw e;
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveFailedPayment(Payment payment) {
        payment.setPaymentStatus(PaymentStatus.FAILURE);
        paymentRepository.save(payment);
    }
}