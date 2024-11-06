package faang.school.accountservice.handler;

import faang.school.accountservice.dto.account.AccountDto;


public class AccountAmountHandler implements RequestTaskHandler<AccountDto> {

    @Override
    public void execute(AccountDto accountDto) {
// отсутсвует UserClient, поэтому проверить кол-во счетов невозможно
    }

    @Override
    public Long getHandlerId() {
        return 1L;
    }
}
