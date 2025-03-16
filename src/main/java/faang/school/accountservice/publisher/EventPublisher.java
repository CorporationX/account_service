package faang.school.accountservice.publisher;

public interface EventPublisher<T> {
    void publish(T event);
}
