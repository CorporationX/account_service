package faang.school.accountservice.publisher.payment.abstracts;

public interface PaymentPublisher<T> {
    Class<T> getInstance();

    void makeResponse(Object... args);

    void makeErrorResponse(Object... args);

    String getTopicName();

    void publish();
}
