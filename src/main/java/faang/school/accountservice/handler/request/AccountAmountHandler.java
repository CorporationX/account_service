package faang.school.accountservice.handler.request;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.enums.RequestHandlerType;
import org.springframework.stereotype.Service;

@Service
public class AccountAmountHandler implements RequestTaskHandler<AccountDto> {

    @Override
    public void execute(AccountDto accountDto) {
// отсутсвует UserClient, поэтому проверить кол-во счетов невозможно
    }

    @Override
    public RequestHandlerType getHandlerId() {
        return RequestHandlerType.AMOUNT_HANDLER;
    }
}
