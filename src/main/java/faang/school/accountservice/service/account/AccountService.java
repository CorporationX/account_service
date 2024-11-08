package faang.school.accountservice.service.account;

import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.dto.account.CreateAccountDto;
import faang.school.accountservice.dto.account.UpdateAccountDto;
import faang.school.accountservice.dto.filter.AccountFilterDto;
import org.springframework.data.domain.Page;


public interface AccountService {

    Page<AccountDto> getAccounts(AccountFilterDto filterDto, int page, int size);

    AccountDto getAccount(Long accountId);

    AccountDto updateAccount(Long accountId, UpdateAccountDto updateAccountDto);

    AccountDto createAccount(CreateAccountDto accountDto);
}
