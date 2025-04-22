package faang.school.accountservice.service.account;

import faang.school.accountservice.dto.AccountRequestDto;
import faang.school.accountservice.dto.AccountResponseDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.exception.AccountNotFoundException;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

import static faang.school.accountservice.messages.ErrorMessages.ACCOUNT_NOT_FOUND;

@RequiredArgsConstructor
@Service
@Slf4j
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    @Override
    public AccountResponseDto getAccount(String accountNumber) {
        return accountMapper.toAccountDto(findAccount(accountNumber));
    }

    @Override
    public AccountResponseDto createAccount(AccountRequestDto accountRequest) {
        String accountNumber = generateAccountNumber();

    }

    private Account findAccount(String accountNumber) {
        return accountRepository.findById(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(ACCOUNT_NOT_FOUND.formatted(accountNumber)));
    }

    private String generateAccountNumber() {
        SecureRandom secureRandom = new SecureRandom();
        int length = secureRandom.nextInt(9) + 12;

        StringBuilder accountNumber = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            accountNumber.append(secureRandom.nextInt(10));
        }
        return accountNumber.toString();
    }
}
