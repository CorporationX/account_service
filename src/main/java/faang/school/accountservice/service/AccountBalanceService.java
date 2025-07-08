package faang.school.accountservice.service;

import faang.school.accountservice.dto.AccountBalanceDto;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.event.BalanceChangeEvent;
import faang.school.accountservice.mapper.AccountBalanceMapper;
import faang.school.accountservice.mapper.BalanceAuditMapper;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.AccountBalance;
import faang.school.accountservice.model.BalanceAudit;
import faang.school.accountservice.model.TransactionType;
import faang.school.accountservice.repository.AccountBalanceRepository;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.BalanceAuditRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountBalanceService {
    private final AccountBalanceRepository balanceRepository;
    private final AccountBalanceMapper accountBalanceMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional(readOnly = true)
    public AccountBalanceDto get(Long accountId) {
        log.info("Starting method get with account ID: {}", accountId);
        AccountBalance accountBalance = balanceRepository.findByAccountId(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account balance not found!"));
        log.info("Successfully retrieved account balance!");

        AccountBalanceDto dto = accountBalanceMapper.toDto(accountBalance);
        dto.setAccountNumber(accountBalance.getAccount().getAccountNumber());
        return dto;
    }

    @Transactional()
    public AccountBalanceDto createBalance(Account account, String currency) {
        log.info("Starting method createBalance with account ID: {}, currency: {}", account.getId(), currency);
        Currency.valueOf(currency.toUpperCase());
        log.info("Successfully validated currency: {}", currency);

        if (!Objects.isNull(account.getAccountNumber()) && !account.getAccountNumber().isBlank()) {
            log.info("Successfully validated account number: {}", account.getAccountNumber());

            AccountBalance newAccountBalance = AccountBalance
                    .builder()
                    .account(account)
                    .authorizedBalance(BigDecimal.ZERO)
                    .actualBalance(BigDecimal.ZERO)
                    .currency(currency)
                    .build();

            AccountBalance saved = balanceRepository.saveAndFlush(newAccountBalance);
            log.info("Successfully created new account balance: {}", newAccountBalance);

            UUID operationId = UUID.randomUUID();
            eventPublisher.publishEvent(new BalanceChangeEvent(saved, operationId));
            return accountBalanceMapper.toDto(saved);
        }
        log.error("This account does not have an account number!");
        throw new IllegalArgumentException("This account does not have an account number!");
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public AccountBalanceDto updateBalance(Account account, BigDecimal amount,
                                           String currency, TransactionType type) {
        log.info("Starting method updateBalance with account ID: {}, amount: {}, currency: {}, type: {}",
                account.getId(), amount, currency, type);

        AccountBalance accountBalance = account.getBalance();
        if (!accountBalance.getCurrency().equals(currency)) {
            log.error("Currency mismatch: balance currency={} vs request currency={}",
                    accountBalance.getCurrency(), currency);
            throw new IllegalArgumentException("Your account balance currency does not match the transaction currency!");
        }

        switch (type) {
            case OUTPUT -> {
                if (accountBalance.getActualBalance().compareTo(amount) < 0) {
                    log.error("Insufficient funds: available={}, required={}",
                            accountBalance.getActualBalance(), amount);
                    throw new IllegalArgumentException("Failed to update balance, insufficient funds!");
                }
                accountBalance.setActualBalance(accountBalance.getActualBalance().subtract(amount));
                accountBalance.setAuthorizedBalance(accountBalance.getAuthorizedBalance().add(amount));
                log.info("OUTPUT applied: new actual={}, new authorized={}",
                        accountBalance.getActualBalance(), accountBalance.getAuthorizedBalance());
            }
            case INPUT -> {
                accountBalance.setActualBalance(accountBalance.getActualBalance().add(amount));
                log.info("INPUT applied: new actual={}", accountBalance.getActualBalance());
            }
            default -> {
                log.error("Unknown transaction type: {}", type);
                throw new IllegalArgumentException("Failed to update balance, invalid transaction type!");
            }
        }
        AccountBalance updated = balanceRepository.saveAndFlush(accountBalance);

        UUID operationId = UUID.randomUUID();
        eventPublisher.publishEvent(new BalanceChangeEvent(updated, operationId));
        log.info("Audit record created [balanceId={}, version={}, operationId={}]",
                updated.getId(), updated.getVersion(), operationId);

        return accountBalanceMapper.toDto(updated);
    }
}
