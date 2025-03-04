package faang.school.accountservice.service;

import faang.school.accountservice.dto.account.AccountCreateDto;
import faang.school.accountservice.dto.account.AccountReadDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.exception.BusinessException;
import faang.school.accountservice.exception.EntityNotFoundException;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.repository.AccountRepository;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final UserService userService;
    private final ProjectService projectService;

    public AccountReadDto getAccount(String invoice) {
        Account account = findByInvoice(invoice);
        return accountMapper.toDto(account);
    }

    public AccountReadDto openAccount(AccountCreateDto dto) {
        boolean userIsNull = dto.getUserId() == null;
        boolean projectIsNull = dto.getProjectId() == null;
        validateAccountCreationOwner(userIsNull, projectIsNull);

        Account account = accountMapper.toEntity(dto);
        if (!userIsNull) {
            userService.getUserById(dto.getUserId());
            account.setAuthorId(dto.getUserId());
        } else {
            projectService.getProjectById(dto.getProjectId());
            account.setProjectId(dto.getProjectId());
        }
        account.setAccountNumber(UUID.randomUUID().toString());

        return accountMapper.toDto(accountRepository.save(account));
    }

    @Transactional
    @Retryable(
            retryFor = OptimisticLockException.class,
            backoff = @Backoff(delay = 100)
    )
    public AccountReadDto freezeInvoice(String invoice) {
        Account account = findByInvoice(invoice);
        validateAccountFreezing(account);
        account.setStatus(AccountStatus.FROZEN);
        return accountMapper.toDto(account);
    }

    @Transactional
    @Retryable(
            retryFor = OptimisticLockException.class,
            backoff = @Backoff(delay = 100)
    )
    public AccountReadDto closeInvoice(String invoice) {
        Account account = findByInvoice(invoice);
        validateAccountClosing(account);
        account.setStatus(AccountStatus.CLOSE);
        account.setClosedAt(LocalDateTime.now());
        return accountMapper.toDto(account);
    }

    private Account findByInvoice(String invoice) {
        return accountRepository.findByAccountNumber(invoice)
                .orElseThrow(() ->
                        new EntityNotFoundException("Такой счёт не найден")
                );
    }

    private void validateAccountClosing(Account account) {
        if (account.getStatus().equals(AccountStatus.CLOSE)) {
            throw new BusinessException("Счёт уже закрыт");
        }
    }

    private void validateAccountFreezing(Account account) {
        if (account.getStatus().equals(AccountStatus.CLOSE)) {
            throw new BusinessException("Нельзя заморозить закрытый счёт");
        }
        if (account.getStatus().equals(AccountStatus.FROZEN)) {
            throw new BusinessException("Счёт уже заморожен");
        }
    }

    private void validateAccountCreationOwner(
            boolean userIsNull, boolean projectIsNull
    ) {
        if (userIsNull == projectIsNull) {
            throw new BusinessException(
                    "Создать счёт может либо пользователь, либо проект"
            );
        }
    }
}
