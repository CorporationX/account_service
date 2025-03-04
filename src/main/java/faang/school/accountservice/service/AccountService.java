package faang.school.accountservice.service;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.entity.Account;
import org.springframework.web.bind.annotation.RequestBody;

public interface AccountService {
    Account createAccount(AccountDto accountDto);

    Account blockAccount(Long id);

    Account closeAccount(Long id);
}
