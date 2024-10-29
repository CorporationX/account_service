package faang.school.accountservice.handler.request;

import faang.school.accountservice.dto.AccountDto;

public interface RequestTaskHandler {
    void execute(AccountDto accountDto);

    Long getHandlerId();
}
