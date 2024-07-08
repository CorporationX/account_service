package faang.school.accountservice.service.payment;

public interface PaymentService {

    void authorizePayment(Long userId, Long paymentId);

    void cancelPayment(Long userId, Long paymentId);

    void clearPayment(Long paymentId);
}
