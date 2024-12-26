package faang.school.accountservice.publisher;

public interface MessagePublisher <T> {

    void publish(T event);
}
