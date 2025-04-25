package faang.school.accountservice.service.implementations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.AccountRequest;
import faang.school.accountservice.dto.AccountResponse;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.exception.InternalException;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.service.interfaces.AccountOwnerService;
import faang.school.accountservice.service.interfaces.AccountService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final ObjectMapper objectMapper;
    private final FreeAccountNumberServiceImpl numberService;
    private final AccountOwnerService accountOwnerService;

    @Override
    @Transactional(readOnly = true)
    public AccountResponse getAccount(long id) {
        Account account = getAccountById(id);
        log.info("Successfully got account with id: {}", id);
        return accountMapper.toDto(account);
    }

    @Override
    @Transactional
    public AccountResponse createAccount(AccountRequest accountRequest) {
        try {
            String requestJson = objectMapper.writeValueAsString(accountRequest);
            log.info("Received account creation request: {}", requestJson);
            Account account = Account.builder()
                    .accountNumber(numberService.generateAccountNumber(accountRequest.getType()))
                    .type(accountRequest.getType())
                    .currency(accountRequest.getCurrency())
                    .status(AccountStatus.ACTIVE)
                    .owner(accountOwnerService.findOwner(accountRequest.getOwnerId(), accountRequest.getOwnerType()))
                    .build();
            Account savedAccount = accountRepository.save(account);
            log.info("Successfully created account with id: {}", savedAccount.getId());
            return accountMapper.toDto(savedAccount);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize AccountRequest to JSON", e);
            throw new InternalException("Error processing account request: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    @Retryable(
            retryFor = OptimisticLockingFailureException.class,
            backoff = @Backoff(delay = 1000)
    )
    public AccountResponse blockAccount(long id) {
        log.info("Blocking account with id: {}", id);
        Account account = getAccountById(id);
        if (account.getStatus() == AccountStatus.BLOCKED) {
            throw new IllegalArgumentException("Account is already blocked");
        }
        account.setStatus(AccountStatus.BLOCKED);
        account = accountRepository.save(account);
        log.info("Successfully blocked account with id: {}", id);
        return accountMapper.toDto(account);
    }

    @Override
    @Transactional
    @Retryable(
            retryFor = OptimisticLockingFailureException.class,
            backoff = @Backoff(delay = 1000)
    )
    public AccountResponse closeAccount(long id) {
        log.info("Closing account with id: {}", id);
        Account account = getAccountById(id);
        if (account.getStatus() == AccountStatus.CLOSED) {
            throw new IllegalArgumentException("Account is already closed");
        }
        account.setStatus(AccountStatus.CLOSED);
        account.setClosedAt(LocalDateTime.now());
        account = accountRepository.save(account);
        log.info("Successfully closed account with id: {}", id);
        return accountMapper.toDto(account);
    }

    public Account getAccountById(long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Account with id: %d was not found", accountId)));
    }
}
