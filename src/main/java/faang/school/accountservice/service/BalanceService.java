package faang.school.accountservice.service;

import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.BalanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BalanceService {

    private final BalanceRepository balanceRepository;
    private final AccountRepository accountRepository;

    public Balance createBalance(UUID accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        Balance balance = new Balance();
        balance.setAccount(account);
        balance.setAuthorizedBalance(BigDecimal.ZERO);
        balance.setActualBalance(BigDecimal.ZERO);

        return balanceRepository.save(balance);
    }

    public Balance updateBalance(Long accountId, BigDecimal newAuthorized, BigDecimal newActual) {
        Balance balance = balanceRepository.findByAccountId(accountId)
                .orElseThrow(() -> new RuntimeException("Balance not found"));

        if (newAuthorized != null) {
            balance.setAuthorizedBalance(newAuthorized);
        }
        if (newActual != null) {
            balance.setActualBalance(newActual);
        }

        return balanceRepository.save(balance);
    }

    public Balance getBalance(Long accountId) {
        return balanceRepository.findByAccountId(accountId)
                .orElseThrow(() -> new RuntimeException("Balance not found"));
    }
}
