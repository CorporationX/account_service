package faang.school.accountservice.service;

import faang.school.accountservice.model.account.Account;
import faang.school.accountservice.model.balance.Balance;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.BalanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BalanceService {

    private final AccountRepository accountRepository;
    private final BalanceRepository balanceRepository;

    @Transactional
    public Balance createBalance() {
        Balance balance = Balance.builder()
                .authorization(0.0)
                .actual(0.0)
                .build();

        return balanceRepository.save(balance);
    }

    @Transactional
    public Balance updateBalance(UUID accountUuid, Balance balance) {
        Account account = accountRepository.findById(accountUuid)
                .orElseThrow();

        Balance storedBalance = account.getBalance();
        storedBalance.setAuthorization(balance.getAuthorization());
        storedBalance.setActual(balance.getActual());

        return balanceRepository.save(storedBalance);
    }

    @Transactional(readOnly = true)
    public Balance getBalance(UUID accountUuid) {
        Account account = accountRepository.findById(accountUuid)
                .orElseThrow();

        return account.getBalance();
    }
}
