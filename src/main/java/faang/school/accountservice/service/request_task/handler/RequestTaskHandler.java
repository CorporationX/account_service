package faang.school.accountservice.service.request_task.handler;

public interface RequestTaskHandler {

    void execute(String context);

    long getHandlerId();
}