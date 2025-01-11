package faang.school.accountservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.AccountRequest;
import faang.school.accountservice.dto.AccountResponse;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.AccountOwner;
import faang.school.accountservice.entity.Request;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.request.RequestType;
import faang.school.accountservice.exception.JsonMappingException;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.service.request.RequestService;
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
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final ObjectMapper objectMapper;
    private final RequestService requestService;
    private final FreeAccountNumbersService numbersService;
    private final AccountOwnerService accountOwnerService;

    @Transactional(readOnly = true)
    public AccountResponse getAccount(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        log.info("Successfully get account with id: {}", id);
        return accountMapper.toDto(account);
    }

    @Transactional
    public void createAccountRequest(AccountRequest accountRequest) {
        Request request = requestService.
                createRequest(RequestType.CREATE_ACCOUNT, accountRequest.getScheduledAt());
        try {
            String requestContext = objectMapper.writeValueAsString(accountRequest);
            request.setContext(requestContext);
        } catch (JsonProcessingException e) {
            throw new JsonMappingException(e.getMessage());
        }
        requestService.updateRequest(request);
    }

    @Transactional
    public Account createAccount(Request request) {
        AccountRequest accountRequest = mapAccountRequest(request);
        String number = numbersService.getFreeAccountNumber(accountRequest.getType());
        AccountOwner owner = accountOwnerService.findOwner(accountRequest.getOwnerId(),
                accountRequest.getOwnerType());

        Account account = Account.builder()
                .accountNumber(number)
                .type(accountRequest.getType())
                .currency(accountRequest.getCurrency())
                .status(AccountStatus.ACTIVE)
                .owner(owner)
                .build();

        return accountRepository.save(account);
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

    public Account getAccountById(long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Account with ID=%d was not found".formatted(accountId)));
    }

    public void deleteAccount(Long id) {
        accountRepository.deleteById(id);
        log.info("Deleting account with id: {}", id);
    }

    private Account getAccountEntity(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
    }

    private AccountRequest mapAccountRequest(Request request) {
        AccountRequest accountRequest;
        try {
            accountRequest = objectMapper.readValue(request.getContext(), AccountRequest.class);
        } catch (JsonProcessingException e) {
            throw new JsonMappingException(e.getMessage());
        }
        return accountRequest;
    }
}
