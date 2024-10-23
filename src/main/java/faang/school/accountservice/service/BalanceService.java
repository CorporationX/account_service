package faang.school.accountservice.service;

import faang.school.accountservice.model.account.Account;
import faang.school.accountservice.model.balance.Balance;
import faang.school.accountservice.reposiory.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BalanceService {

    private final AccountRepository accountRepository;

    @Transactional
    public Balance createBalance(Balance balance) {
        Account account = accountRepository.findById(balance.getAccount().getId())
                .orElseThrow();

        account.setBalance(balance);
        Account savedAccount = accountRepository.save(account);

        return savedAccount.getBalance();
    }

    @Transactional
    public Balance updateBalance(Balance balance) {
        Account account = accountRepository.findById(balance.getAccount().getId())
                .orElseThrow();

        Balance storedBalance = account.getBalance();
        storedBalance.setAuthorization(balance.getAuthorization());
        storedBalance.setActual(balance.getActual());
        storedBalance.setVersion(balance.getVersion() + 1);

        Account savedAccount = accountRepository.save(account);

        return savedAccount.getBalance();
    }

    @Transactional(readOnly = true)
    public Balance getBalance(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow();

        return account.getBalance();
    }
}
