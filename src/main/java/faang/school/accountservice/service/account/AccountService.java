package faang.school.accountservice.service.account;

import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.enums.account.AccountStatus;
import faang.school.accountservice.exception.account.AccountNotFoundException;
import faang.school.accountservice.mapper.account.AccountMapper;
import faang.school.accountservice.repository.account.AccountRepository;
import faang.school.accountservice.validator.account.AccountValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepo;
    private final AccountValidator validator;
    private final AccountMapper mapper;

    @Transactional
    public AccountDto get(Long id) {
        return mapper.toDto(getAccount(id));
    }

    @Transactional
    public AccountDto openAccount(AccountDto accountDto) {
        validator.checkOpening(accountDto);
        //метод для генерации номера платежного счета
        Account account = mapper.toEntity(accountDto);
        return mapper.toDto(accountRepo.save(account));
    }

    @Transactional
    public AccountDto freezeAccount(Long id) {
        return setAccountStatus(id, AccountStatus.FROZEN);
    }

    @Transactional
    public AccountDto closeAccount(Long id) {
        return setAccountStatus(id, AccountStatus.CLOSED);
    }

    @Transactional
    public List<AccountDto> getAllOfUser(Long userId) {
        validator.checkUserId(userId);
        return mapper.toDto(accountRepo.findByOwnerUserId(userId));
    }

    @Transactional
    public List<AccountDto> getAllOfProject(Long projectId) {
        validator.checkProjectId(projectId);
        return mapper.toDto(accountRepo.findByOwnerProjectId(projectId));
    }

    private Account getAccount(Long id) {
        return accountRepo.findById(id)
                .orElseThrow(() ->
                        {
                            log.error("Account doesn't exist.");
                            return new AccountNotFoundException("Account doesn't exist.");
                        }
                );
    }

    private AccountDto setAccountStatus(Long id, AccountStatus status) {
        Account account = getAccount(id);
        account.setStatus(status);
        account.setClosedAt(LocalDateTime.now());
        return mapper.toDto(accountRepo.save(account));
    }

}


