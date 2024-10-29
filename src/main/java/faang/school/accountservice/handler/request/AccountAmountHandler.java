package faang.school.accountservice.handler.request;

import faang.school.accountservice.dto.AccountDto;
import org.springframework.stereotype.Service;

@Service
public class AccountAmountHandler implements RequestTaskHandler {

    @Override
    public void execute(AccountDto accountDto) {
// отсутсвует UserClient, поэтому проверить кол-во счетов невозможно
    }

    @Override
    public Long getHandlerId() {
        return 1L;
    }
}
