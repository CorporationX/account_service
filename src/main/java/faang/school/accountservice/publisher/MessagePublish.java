package faang.school.accountservice.publisher;

public interface MessagePublish<T> {
    void publish(T event);
}
