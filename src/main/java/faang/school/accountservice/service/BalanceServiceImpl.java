package faang.school.accountservice.service;

import faang.school.accountservice.exception.ResourceNotFoundException;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.Balance;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.BalanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class BalanceServiceImpl implements BalanceService{

    private final AccountRepository accountRepository;
    private final BalanceRepository balanceRepository;

    @Transactional
    @Override
    public Balance createBalance(Long accountId){
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
        Balance balance = new Balance();
        balance.setAccount(account);
        balance.setCurrentBalance(BigDecimal.ZERO);
        balance.setAuthorizedBalance(BigDecimal.ZERO);
        return balanceRepository.save(balance);
    }

    @Override
    public Balance getBalance(Long accountId){
        return balanceRepository.findByAccountId(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Balance not found for account"));
    }

    @Transactional
    @Override
    public Balance updateBalance(Long accountId, BigDecimal newCurrentBalance, BigDecimal newAuthorizedBalance){
        Balance balance = balanceRepository.findByAccountId(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Balance not found for account"));
        balance.setCurrentBalance(newCurrentBalance);
        balance.setAuthorizedBalance(newAuthorizedBalance);
        return balanceRepository.save(balance);
    }
}
