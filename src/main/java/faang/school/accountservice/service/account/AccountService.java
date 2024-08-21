package faang.school.accountservice.service.account;

import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.mapper.account.AccountMapper;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.validator.account.AccountValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final AccountValidator accountValidator;

    @Transactional
    public AccountDto getAccount(long ownerId) {
        return accountMapper.toDto(
                accountRepository.findById(ownerId)
                        .orElseThrow(() -> {
                            log.error("Владельца с таким id не существует");
                            return new IllegalArgumentException("Владельца с таким id не существует");
                        }));
    }

    @Transactional
    public AccountDto openAccount(AccountDto accountDto) {
        Account account = accountMapper.toEntity(accountDto);

        accountValidator.checkExistenceOfTheNumber(account);
        accountRepository.save(account);

        return accountMapper.toDto(account);
    }

    @Transactional
    public AccountDto updateAccount(long ownerId, String status) {
        Account account = accountRepository.findById(ownerId)
                .orElseThrow(() -> {
                    log.error("Владельца с таким id не существует: {}", ownerId);
                    return new IllegalArgumentException("Владельца с таким id не существует");
                });

        AccountStatus newStatus = accountValidator.checkForMatchStatusAndTransformation(status);

        if (!account.getAccountStatus().equals(newStatus)) {
            account.setAccountStatus(newStatus);
            if (newStatus == AccountStatus.CLOSED) {
                account.setCloseAt(LocalDateTime.now());
            }
            accountRepository.save(account);
            log.info("Статус аккаунта с ID {} был изменён на {}", ownerId, newStatus);
        }

        return accountMapper.toDto(account);
    }
}