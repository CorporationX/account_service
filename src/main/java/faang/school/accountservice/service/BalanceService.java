package faang.school.accountservice.service;

import faang.school.accountservice.model.account.Account;
import faang.school.accountservice.model.balance.Balance;
import faang.school.accountservice.reposiory.AccountRepository;
import faang.school.accountservice.reposiory.BalanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BalanceService {

    private final AccountRepository accountRepository;
    private final BalanceRepository balanceRepository;

    @Transactional
    public Balance createBalance(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow();

        Balance newBalance = Balance.builder()
                .authorization(0.0)
                .actual(0.0)
                .account(account)
                .build();

        account.setBalance(newBalance);
         accountRepository.save(account);

        return newBalance;
    }

    @Transactional
    public Balance updateBalance(Balance balance) {
        Account account = accountRepository.findById(balance.getAccount().getId())
                .orElseThrow();

        Balance storedBalance = account.getBalance();
        storedBalance.setAuthorization(balance.getAuthorization());
        storedBalance.setActual(balance.getActual());

        return balanceRepository.save(storedBalance);
    }

    @Transactional(readOnly = true)
    public Balance getBalance(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow();

        return account.getBalance();
    }
}
