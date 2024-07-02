package faang.school.accountservice.service.payment;

public interface PaymentService {

    void createPayment(Long paymentId);

    void cancelPayment(Long userid, Long transactionId);

    void clearPayment(Long paymentId);
}
