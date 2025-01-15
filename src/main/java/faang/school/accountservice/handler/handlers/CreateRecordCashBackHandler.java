package faang.school.accountservice.handler.handlers;

import faang.school.accountservice.entity.Request;
import faang.school.accountservice.entity.RequestTask;
import faang.school.accountservice.enums.RequestHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CreateRecordCashBackHandler implements RequestTaskHandler {
    @Override
    public RequestHandler getHandlerId() {
        return RequestHandler.CREATE_CASHBACK_RECORD;
    }

    @Async("createRecordCashBackHandlerExecutor")
    @Override
    public void execute(Request request, RequestTask task) {

    }

    @Override
    public void rollback(Request request, RequestTask task) {
        log.info("Try rollback creation cashback record for account with id: {}", request.getContext().get("accountId"));
    }
}
