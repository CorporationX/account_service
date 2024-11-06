package faang.school.accountservice.handler;

public interface RequestTaskHandler<T> {
    void execute(T t);

    Long getHandlerId();
}
