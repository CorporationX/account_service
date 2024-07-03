package faang.school.accountservice.service.payment;

import faang.school.accountservice.event.CancelPaymentEvent;
import faang.school.accountservice.event.NewPaymentEvent;
import faang.school.accountservice.exception.DataValidationException;
import faang.school.accountservice.exception.NotFoundException;
import faang.school.accountservice.model.Balance;
import faang.school.accountservice.model.Payment;
import faang.school.accountservice.model.enums.PaymentStatus;
import faang.school.accountservice.publisher.CancelPaymentPublisher;
import faang.school.accountservice.publisher.NewPaymentPublisher;
import faang.school.accountservice.repository.BalanceRepository;
import faang.school.accountservice.repository.PaymentRepository;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final BalanceRepository balanceRepository;
    private final NewPaymentPublisher newPaymentPublisher;
    private final CancelPaymentPublisher cancelPaymentPublisher;

    @Transactional
    @Retryable(retryFor = OptimisticLockException.class, maxAttempts = 3, backoff = @Backoff(delay = 300))
    public void authorizePayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NotFoundException("Payment not found"));

        try {
            Balance senderBalance = balanceRepository.findBalanceByAccountNumber(payment.getSenderAccountNumber())
                    .orElseThrow(() -> new NotFoundException("Sender balance hasn't been found"));
            Balance receiverBalance = balanceRepository.findBalanceByAccountNumber(payment.getReceiverAccountNumber())
                    .orElseThrow(() -> new NotFoundException("Receiver balance hasn't been found"));

            if (senderBalance.getAuthorizationBalance().compareTo(payment.getAmount()) < 0) {
                throw new DataValidationException("Insufficient funds in sender account");
            }

            senderBalance.setAuthorizationBalance(senderBalance.getAuthorizationBalance().subtract(payment.getAmount()));
            receiverBalance.setAuthorizationBalance(receiverBalance.getAuthorizationBalance().add(payment.getAmount()));

            balanceRepository.save(senderBalance);
            balanceRepository.save(receiverBalance);

            payment.setPaymentStatus(PaymentStatus.READY_TO_CLEAR);
            paymentRepository.save(payment);

            NewPaymentEvent event = new NewPaymentEvent(
                    senderBalance.getId(),
                    receiverBalance.getId(),
                    payment.getCurrency(),
                    payment.getAmount()
            );

            newPaymentPublisher.publish(event);
            log.info("Payment with ID={} has been processed and is ready to clear", paymentId);

        } catch (Exception e) {
            payment.setPaymentStatus(PaymentStatus.FAILURE);
            paymentRepository.save(payment);
            log.error("Failed to process payment with ID={}. Error: {}", paymentId, e.getMessage());
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

        senderBalance.setAuthorizationBalance(senderBalance.getAuthorizationBalance().add(payment.getAmount()));
        receiverBalance.setAuthorizationBalance(receiverBalance.getAuthorizationBalance().subtract(payment.getAmount()));
        balanceRepository.save(senderBalance);
        balanceRepository.save(receiverBalance);

        payment.setPaymentStatus(PaymentStatus.CANCELED);
        paymentRepository.save(payment);

        CancelPaymentEvent event = new CancelPaymentEvent(
                senderBalance.getId(),
                receiverBalance.getId(),
                payment.getCurrency(),
                payment.getAmount()
        );

        cancelPaymentPublisher.publish(event);
        log.info("Payment with ID={} has been canceled", paymentId);
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

            senderBalance.setActualBalance(senderBalance.getActualBalance().subtract(payment.getAmount()));
            receiverBalance.setActualBalance(receiverBalance.getActualBalance().add(payment.getAmount()));
            balanceRepository.save(senderBalance);
            balanceRepository.save(receiverBalance);

            payment.setPaymentStatus(PaymentStatus.CLEAR);
            paymentRepository.save(payment);

            log.info("Successfully cleared payment with ID {} and credited amount {} to account {}",
                    paymentId, payment.getAmount(), payment.getReceiverAccountNumber());
        } catch (Exception e) {
            payment.setPaymentStatus(PaymentStatus.FAILURE);
            paymentRepository.save(payment);
            log.error("Failed to process payment with ID={}. Error: {}", paymentId, e.getMessage());
            throw e;
        }
    }
}
