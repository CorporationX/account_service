package faang.school.accountservice.handler.handlers;

import faang.school.accountservice.entity.Request;
import faang.school.accountservice.entity.RequestTask;
import faang.school.accountservice.enums.RequestHandler;
import org.springframework.stereotype.Component;

@Component
public class CreateRecordBalanceHandler implements RequestTaskHandler {
    @Override
    public RequestHandler getHandlerId() {
        return RequestHandler.CREATE_BALANCE_RECORDS;
    }

    @Override
    public void execute(Request request, RequestTask task) {
        Long accountId = (Long) request.getContext().get("accountId");


    }

    @Override
    public void rollback(Request request, RequestTask task) {

    }
}
