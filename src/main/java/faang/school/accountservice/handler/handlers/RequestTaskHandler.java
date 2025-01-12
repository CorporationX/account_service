package faang.school.accountservice.handler.handlers;

import faang.school.accountservice.entity.Request;
import faang.school.accountservice.entity.RequestTask;
import faang.school.accountservice.enums.RequestHandler;

public interface RequestTaskHandler {
    RequestHandler getHandlerId();
    void execute(Request request, RequestTask task);
    void rollback(Request request, RequestTask task);
}
