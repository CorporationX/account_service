package faang.school.accountservice.message.producer;

public interface MessagePublisher {
    void publish(String channel, Object message);
}
