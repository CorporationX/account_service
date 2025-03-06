package faang.school.accountservice.service;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.dto.AccountFilterDto;
import faang.school.accountservice.entity.Account;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface AccountService {
    Account createAccount(AccountDto accountDto);

    Account blockAccount(Long id);

    Account closeAccount(Long id);

    List<AccountDto> getAccountsWithFilters(AccountFilterDto accountFilterDto);
}
