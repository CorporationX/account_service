package faang.school.accountservice.service.request.handler;

import faang.school.accountservice.entity.Request;
import faang.school.accountservice.entity.RequestTask;

public interface RequestTaskHandler {
    void execute(Request request, RequestTask task);

    String getHandlerId();
}