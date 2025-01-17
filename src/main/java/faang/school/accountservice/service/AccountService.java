package faang.school.accountservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.AccountRequest;
import faang.school.accountservice.dto.AccountResponse;
import faang.school.accountservice.dto.TransactionDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.AccountOwner;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.entity.Request;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.OperationType;
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

import java.math.BigDecimal;
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
    private final BalanceService balanceService;

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
        Account account = getAccountById(id);

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
        Account account = getAccountById(id);

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

    @Transactional
    public void deleteAccount(Long id) {
        Account account = getAccountById(id);
        Balance balance = account.getBalance();
        if (checkAccountBalanceAmount(balance) != 0) {
            if (checkAccountBalanceAmount(balance) > 0) {
                transferBalance(account, balance);
            }
            if (checkAccountBalanceAmount(balance) < 0) {
                throw new IllegalStateException("Account balance is negative, you can't close account");
            }
        }
        accountRepository.deleteById(id);
        log.info("Deleting account with id: {}", id);
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

    private void transferBalance(Account account, Balance balance) {
        AccountOwner accountOwner = account.getOwner();
        Account accountToTransferMoney = accountOwner.getAccounts().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Account balance is not empty," +
                        "please transfer money first"));
        TransactionDto gto = TransactionDto.builder()
                .amount(balance.getActualBalance())
                .operationType(OperationType.CLEARING)
                .build();
        balanceService.updateBalance(accountToTransferMoney.getId(), gto);
    }

    private int checkAccountBalanceAmount(Balance balance) {
        return balance.getActualBalance().compareTo(BigDecimal.ZERO);
    }
}
