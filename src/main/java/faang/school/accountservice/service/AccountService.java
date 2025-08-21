package faang.school.accountservice.service;

import faang.school.accountservice.dto.account.AccountCreateDto;
import faang.school.accountservice.dto.account.AccountViewDto;

public interface AccountService {

    AccountCreateDto openAccount();

    AccountViewDto getAccount();

    AccountViewDto blockAccount();

    AccountViewDto closeAccount();
}
