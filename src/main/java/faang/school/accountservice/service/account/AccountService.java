package faang.school.accountservice.service.account;

import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.dto.account.AccountDtoToUpdate;
import org.springframework.stereotype.Component;

@Component
public interface AccountService {
    AccountDto open(AccountDto accountDto);

    AccountDto update(long accountId, AccountDtoToUpdate accountDto);

    AccountDto get(long accountId);

    void block(long accountId);

    void unBlock(long accountId);

    void close(long accountId);
}
