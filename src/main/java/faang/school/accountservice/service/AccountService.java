package faang.school.accountservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.AccountRequest;
import faang.school.accountservice.dto.AccountResponse;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.Request;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.request.RequestType;
import faang.school.accountservice.exception.JsonMappingException;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.service.request.RequestService;
import faang.school.accountservice.service.request_task.handler.impl.create_account.CheckAccountsQuantity;
import faang.school.accountservice.service.request_task.handler.impl.create_account.CreateAccount;
import faang.school.accountservice.service.request_task.handler.impl.create_account.CreateBalanceAndBalanceAudit;
import faang.school.accountservice.service.request_task.handler.impl.create_account.SendCreateAccountNotification;
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
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final ObjectMapper objectMapper;
    private final RequestService requestService;

    private final CheckAccountsQuantity checkAccountsQuantity;
    private final CreateAccount createAccount;
    private final CreateBalanceAndBalanceAudit balanceAudit;
    private final SendCreateAccountNotification accountNotification;

    @Transactional(readOnly = true)
    public AccountResponse getAccount(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        log.info("Successfully get account with id: {}", id);
        return accountMapper.toDto(account);
    }

    @Transactional
    public AccountResponse openAccount(AccountRequest accountRequest) {
        Account account;
        Request request = requestService.createRequest(RequestType.CREATE_ACCOUNT, null);
        try {
            String requestContext = objectMapper.writeValueAsString(accountRequest);
            request.setContext(requestContext);
            checkAccountsQuantity.execute(request);
            createAccount.execute(request);
            account = objectMapper.readValue(request.getContext(), Account.class);
            balanceAudit.execute(request);
            accountNotification.execute(request);
        } catch (JsonProcessingException e) {
            throw new JsonMappingException(e.getMessage());
        }
        return accountMapper.toDto(account);
    }

    @Transactional
    @Retryable(
            retryFor = OptimisticLockingFailureException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000)
    )
    public AccountResponse blockAccount(Long id) {
        log.info("Blocking account with id: {}", id);
        Account account = getAccountEntity(id);

        if (account.getStatus() == AccountStatus.BLOCKED) {
            throw new IllegalStateException("Account is already blocked");
        }

        account.setStatus(AccountStatus.BLOCKED);
        account = accountRepository.save(account);
        log.info("Successfully blocked account with id: {}", id);
        return accountMapper.toDto(account);
    }

    @Transactional
    @Retryable(
            retryFor = OptimisticLockingFailureException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000)
    )
    public AccountResponse closeAccount(Long id) {
        log.info("Closing account with id: {}", id);
        Account account = getAccountEntity(id);

        if (account.getStatus() == AccountStatus.CLOSED) {
            throw new IllegalStateException("Account is already closed");
        }

        account.setStatus(AccountStatus.CLOSED);
        account.setClosedAt(LocalDateTime.now());
        account = accountRepository.save(account);
        log.info("Successfully closed account with id: {}", id);
        return accountMapper.toDto(account);
    }

    private Account getAccountEntity(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
    }
}
