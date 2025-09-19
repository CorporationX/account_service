package faang.school.accountservice.service.account;

import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.dto.account.CreateAccountDto;
import faang.school.accountservice.enums.AccountStatus;
import lombok.NonNull;

public interface AccountService {
    AccountDto create(@NonNull CreateAccountDto createAccountDto);

    AccountDto getAccountById(@NonNull Long id);

    AccountDto updateAccountStatus(@NonNull Long id, @NonNull AccountStatus status);
}
