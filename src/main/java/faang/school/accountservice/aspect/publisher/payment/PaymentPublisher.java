package faang.school.accountservice.aspect.publisher.payment;

public interface PaymentPublisher<T> {
    Class<T> getInstance();

    void makeResponse(Object... args);

    void makeErrorResponse(Object... args);

    String getTopicName();

    void publish();
}
