package faang.school.accountservice.service.account;

import faang.school.accountservice.dto.AccountRequestDto;
import faang.school.accountservice.dto.AccountResponseDto;

public interface AccountService {
    AccountResponseDto getAccount(String accountNumber);

    AccountResponseDto createAccount(AccountRequestDto accountRequest);

    AccountResponseDto blockAccount(String accountNumber);

    AccountResponseDto closeAccount(String accountNumber);
}
