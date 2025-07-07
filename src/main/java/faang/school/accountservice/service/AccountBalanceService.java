package faang.school.accountservice.service;

import faang.school.accountservice.dto.AccountBalanceDto;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.mapper.AccountBalanceMapper;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.AccountBalance;
import faang.school.accountservice.model.TransactionType;
import faang.school.accountservice.repository.AccountBalanceRepository;
import faang.school.accountservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountBalanceService {
    private final AccountBalanceRepository balanceRepository;
    private final AccountRepository accountRepository;
    private final AccountBalanceMapper accountBalanceMapper;

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
            log.info("Successfully created new account balance: {}", newAccountBalance);
            return accountBalanceMapper.toDto(balanceRepository.save(newAccountBalance));
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
        if (accountBalance.getCurrency().equals(currency)) {
            if (type == TransactionType.OUTPUT && accountBalance.getActualBalance().compareTo(amount) < 0) {
                log.info("Sufficient funds for transaction type: {}", type);
                accountBalance.setActualBalance(accountBalance.getActualBalance().subtract(amount));
                accountBalance.setAuthorizedBalance(accountBalance.getAuthorizedBalance().add(amount));
                return accountBalanceMapper.toDto(balanceRepository.save(accountBalance));
            }
            if (type == TransactionType.INPUT) {
                log.info("Sufficient funds for transaction type: {}", type);
                accountBalance.setActualBalance(accountBalance.getActualBalance().add(amount));
                return accountBalanceMapper.toDto(balanceRepository.save(accountBalance));
            }
            log.error("Failed to update balance, invalid transaction type or insufficient funds!");
            throw new IllegalArgumentException("Failed to update balance, invalid transaction type or insufficient funds!");
        }
        log.info("Your account balance currency does not match the transaction currency!");
        throw new IllegalArgumentException("Your account balance currency does not match the transaction currency!");
    }
}
