package faang.school.accountservice.service.payment;

public interface PaymentService {

    void authorizePayment(Long paymentId);

    void cancelPayment(Long userid, Long transactionId);

    void clearPayment(Long paymentId);
}
