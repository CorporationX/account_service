package faang.school.accountservice.handler.request;

import faang.school.accountservice.dto.AccountDto;

public class BalanceAuditRecordHandler implements RequestTaskHandler {

    @Override
    public void execute(AccountDto accountDto) {

    }

    @Override
    public Long getHandlerId() {
        return 4L;
    }
}
