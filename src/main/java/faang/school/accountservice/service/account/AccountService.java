package faang.school.accountservice.service.account;

import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.dto.account.CreateAccountDto;
import faang.school.accountservice.dto.account.UpdateAccountDto;
import faang.school.accountservice.dto.filter.AccountFilterDto;

import java.util.List;

public interface AccountService {

    List<AccountDto> getAllAccounts(AccountFilterDto filterDto);

    AccountDto getAccount(Long accountId);

    void updateAccount(UpdateAccountDto updateAccountDto);

    void createAccount(CreateAccountDto accountDto);
}
