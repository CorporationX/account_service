package faang.school.accountservice.service;

import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.exception.OperationNotAllowed;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.BalanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BalanceService {

    private final BalanceRepository balanceRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public Balance createBalance(UUID accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        Balance balance = Balance.builder()
                .account(account)
                .actualBalance(BigDecimal.ZERO)
                .authorizedBalance(BigDecimal.ZERO)
                .build();

        return balanceRepository.save(balance);
    }

    public Balance getBalance(UUID accountId) {
        return balanceRepository.findByAccountId(accountId)
                .orElseThrow(() -> new RuntimeException("Balance not found"));
    }

    @Transactional
    public Balance authorize(UUID accountId, BigDecimal amount) {

        Balance balance = balanceRepository.findByAccountIdForUpdate(accountId)
                .orElseThrow(() -> new RuntimeException("Balance not found"));

        if (balance.getActualBalance().compareTo(amount) < 0) {
            throw new OperationNotAllowed("Not enough funds for authorization");
        }

        balance.setActualBalance(balance.getActualBalance().subtract(amount));
        balance.setAuthorizedBalance(balance.getAuthorizedBalance().add(amount));

        return balance;
    }

    @Transactional
    public Balance clear(UUID accountId, BigDecimal amount) {

        Balance balance = balanceRepository.findByAccountIdForUpdate(accountId)
                .orElseThrow(() -> new RuntimeException("Balance not found"));

        if (balance.getAuthorizedBalance().compareTo(amount) < 0) {
            throw new OperationNotAllowed("Not enough authorized funds for clearing");
        }

        balance.setAuthorizedBalance(balance.getAuthorizedBalance().subtract(amount));

        return balance;
    }

    @Transactional
    public Balance cancelAuthorization(UUID accountId, BigDecimal amount) {

        Balance balance = balanceRepository.findByAccountIdForUpdate(accountId)
                .orElseThrow(() -> new RuntimeException("Balance not found"));

        if (balance.getAuthorizedBalance().compareTo(amount) < 0) {
            throw new OperationNotAllowed("Not enough authorized funds for cancellation");
        }

        balance.setAuthorizedBalance(balance.getAuthorizedBalance().subtract(amount));
        balance.setActualBalance(balance.getActualBalance().add(amount));

        return balance;
    }
}
