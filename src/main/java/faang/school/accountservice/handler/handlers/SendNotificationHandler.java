package faang.school.accountservice.handler.handlers;

import faang.school.accountservice.entity.Request;
import faang.school.accountservice.entity.RequestTask;
import faang.school.accountservice.enums.RequestHandler;
import org.springframework.stereotype.Component;

@Component
public class SendNotificationHandler implements RequestTaskHandler{
    @Override
    public RequestHandler getHandlerId() {
        return RequestHandler.SEND_NOTIFICATION;
    }

    @Override
    public void execute(Request request, RequestTask task) {

    }

    @Override
    public void rollback(Request request, RequestTask task) {

    }
}
