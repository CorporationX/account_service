package faang.school.accountservice.service.request_task.handler.impl.create_account;

import faang.school.accountservice.service.request_task.handler.RequestTaskHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CheckAccountsQuantity implements RequestTaskHandler {

    @Override
    public void execute() {

    }

    @Override
    public long getHandlerId() {
        return 1;
    }
}
