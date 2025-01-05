package faang.school.accountservice.service;

import faang.school.accountservice.client.project.ProjectFeignClient;
import faang.school.accountservice.client.user.UserFeignClient;
import faang.school.accountservice.dto.account.AccountReq;
import faang.school.accountservice.dto.account.AccountResp;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.repository.AccountRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final UserFeignClient userFeignClient;
    private final ProjectFeignClient projectFeignClient;

    public AccountResp getAccount(Long id) {
        return accountMapper.accountToAccountResp(getAccountById(id));
    }

    public void openAccount(AccountReq accountReq, boolean isProjectAccount) {
        if (isProjectAccount) {
            checkProject(accountReq.getProjectOwnerId());
        } else {
            checkUser(accountReq.getUserOwnerId());
        }
        Account account = accountMapper.accountReqToAccount(accountReq);
        account.setAccountStatus(AccountStatus.OPEN);
        accountRepository.save(account);
    }

    public void blockAccount(Long id) {
        Account account = getAccountById(id);
        account.setAccountStatus(AccountStatus.BLOCKED);
        accountRepository.save(account);
    }

    public void closeAccount(Long id) {
        Account account = getAccountById(id);
        account.setClosedAt(LocalDateTime.now());
        account.setAccountStatus(AccountStatus.CLOSED);
        accountRepository.save(account);
    }

    private Account getAccountById(Long id) {
        return accountRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Account not found by id: %s", id)));
    }

    private void checkUser(Long userOwnerId) {
        if (userOwnerId == null) {
            log.error("userOwnerId must not be null");
            throw new IllegalArgumentException("userOwnerId must not be null");
        }
        if (userFeignClient.getUser(userOwnerId) == null) {
            log.error("User with id {} does not exist", userOwnerId);
            throw new EntityNotFoundException(String.format("User with id %s does not exist", userOwnerId));
        }
    }

    private void checkProject(Long projectOwnerId) {
        if (projectOwnerId == null) {
            log.error("projectOwnerId must not be null");
            throw new IllegalArgumentException("projectOwnerId must not be null");
        }
        if (projectFeignClient.getProject(projectOwnerId) == null) {
            log.error("Project with id {} does not exist", projectOwnerId);
            throw new EntityNotFoundException(String.format("Project with id %s does not exist", projectOwnerId));
        }
    }
}
