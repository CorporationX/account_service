package faang.school.accountservice.service;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.dto.AccountFilterDto;
import faang.school.accountservice.entity.Account;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface AccountService {

    AccountDto createAccount(AccountDto accountDto);

    AccountDto blockAccount(Long id);

    AccountDto closeAccount(Long id);

    List<AccountDto> getAccountsWithFilters(AccountFilterDto accountFilterDto);

}
