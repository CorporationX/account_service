package faang.school.accountservice.service.request_task;

import faang.school.accountservice.entity.Request;

public interface RequestTaskHandler {

    void execute(Request request);

    long getHandlerId();
}