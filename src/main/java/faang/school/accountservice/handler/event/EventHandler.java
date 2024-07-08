package faang.school.accountservice.handler.event;

public interface EventHandler<T> {
    boolean canHandle(T event);
    void handle(T event);
}