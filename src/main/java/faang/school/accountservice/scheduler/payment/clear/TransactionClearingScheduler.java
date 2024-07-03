package faang.school.accountservice.scheduler.payment.clear;

import faang.school.accountservice.model.Payment;
import faang.school.accountservice.repository.PaymentRepository;
import faang.school.accountservice.service.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionClearingScheduler {

    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;

    @Scheduled(fixedRate = 5000)
    public void clearTransactions() {
        List<Payment> payments = paymentRepository.findReadyToClearTransactions();

        if (!payments.isEmpty()) {
            for (Payment payment : payments) {
                try {
                    paymentService.clearPayment(payment.getId());
                } catch (Exception e) {
                    log.error("Failed to clear transaction with ID {}: {}", payment.getId(), e.getMessage());
                }
            }
        }
    }
}
