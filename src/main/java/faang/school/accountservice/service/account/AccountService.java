package faang.school.accountservice.service.account;

import faang.school.accountservice.dto.account.AccountRequestDto;
import faang.school.accountservice.dto.account.AccountResponseDto;
import jakarta.validation.constraints.Positive;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {
    public AccountResponseDto get(@Positive long id) {
        return null;
    }

    public AccountResponseDto open(AccountRequestDto dto) {
        return null;
    }

    public List<AccountResponseDto> getAccountsByOwner(AccountRequestDto dto) {
        return null;
    }

    public AccountResponseDto blockAccount(AccountRequestDto dto) {
        return null;
    }

    public AccountResponseDto closeAccount(AccountRequestDto dto) {
        return null;
    }
}
