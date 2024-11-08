package faang.school.accountservice.handler;

import faang.school.accountservice.entity.Request;
import faang.school.accountservice.entity.RequestTask;

public interface RequestTaskHandler {
    void execute(Request request, RequestTask task);

    Long getHandlerId();
}
