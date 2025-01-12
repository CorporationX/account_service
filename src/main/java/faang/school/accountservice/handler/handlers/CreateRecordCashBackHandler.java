package faang.school.accountservice.handler.handlers;

import faang.school.accountservice.entity.Request;
import faang.school.accountservice.entity.RequestTask;
import faang.school.accountservice.enums.RequestHandler;
import org.springframework.stereotype.Component;

@Component
public class CreateRecordCashBackHandler implements RequestTaskHandler {
    @Override
    public RequestHandler getHandlerId() {
        return RequestHandler.CREATE_CASHBACK_RECORD;
    }

    @Override
    public void execute(Request request, RequestTask task) {

    }

    @Override
    public void rollback(Request request, RequestTask task) {

    }
}
