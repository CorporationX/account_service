package faang.school.accountservice.service;

import faang.school.accountservice.dto.account.AccountCreateDto;
import faang.school.accountservice.dto.account.AccountReadDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.exception.BusinessException;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.repository.AccountRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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
        return accountMapper.toDto(accountRepository.findByInvoice(invoice));
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
        account.setInvoice(UUID.randomUUID().toString());

        return accountMapper.toDto(accountRepository.save(account));
    }

    @Transactional
    public AccountReadDto freezeInvoice(String invoice) {
        Account account = accountRepository.findByInvoice(invoice);
        if (account.getStatus().equals(AccountStatus.CLOSE)) {
            throw new BusinessException("Нельзя заморозить закрытый счёт");
        }
        account.setStatus(AccountStatus.FROZEN);
        return accountMapper.toDto(account);
    } //TODO сделать обработку OptimisticLockException

    @Transactional
    public AccountReadDto closeInvoice(String invoice) {
        Account account = accountRepository.findByInvoice(invoice);
        account.setStatus(AccountStatus.CLOSE);
        account.setClosedAt(LocalDateTime.now());
        return accountMapper.toDto(account);
    } //TODO сделать обработку OptimisticLockException

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
