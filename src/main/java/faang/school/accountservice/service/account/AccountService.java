package faang.school.accountservice.service.account;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

import static faang.school.accountservice.exception.message.AccountExceptionMessage.NON_EXISTING_ACCOUNT_EXCEPTION;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountVerifier accountVerifier;
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    public AccountDto open(AccountDto accountDto) {
        accountVerifier.verifyOwnerExistence(accountDto.getOwnerUserId(), accountDto.getOwnerProjectId());

        Account createdAccount = accountRepository.save(accountMapper.toEntity(accountDto));
        return accountMapper.toDto(createdAccount);
    }

    public AccountDto getAccount(Long accountId) {
        Account account = getAccountModel(accountId);

        return accountMapper.toDto(account);
    }

    public Account getAccountModel(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> {
                    String message = String.format(NON_EXISTING_ACCOUNT_EXCEPTION.getMessage(), accountId);
                    return new NoSuchElementException(message);
                });
    }
}
