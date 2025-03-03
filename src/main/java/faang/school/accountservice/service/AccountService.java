package faang.school.accountservice.service;

import faang.school.accountservice.dto.AccountRequestDto;
import faang.school.accountservice.dto.AccountResponseDto;

public interface AccountService {
    AccountResponseDto get(Long id);

    AccountResponseDto open(AccountRequestDto accountDto);

    void block(Long id);

    void close(Long id);
}
