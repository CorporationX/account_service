package faang.school.accountservice.service;

import faang.school.accountservice.dto.AccountRequestDto;
import faang.school.accountservice.dto.AccountResponseDto;
import faang.school.accountservice.enums.AccountStatus;

public interface AccountService {
    AccountResponseDto get(Long id);

    AccountResponseDto open(AccountRequestDto accountDto);

    void deactivate(Long id, AccountStatus accountStatus);
}
