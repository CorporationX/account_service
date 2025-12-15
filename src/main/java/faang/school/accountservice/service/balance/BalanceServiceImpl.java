package faang.school.accountservice.service.balance;

import faang.school.accountservice.entity.AccountBalance;
import faang.school.accountservice.repository.AccountBalanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class BalanceServiceImpl implements BalanceService {

    private final AccountBalanceRepository accountBalanceRepository;

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getBalance(Long accountId) {
        return accountBalanceRepository.findByAccountId(accountId)
                .map(AccountBalance::getBalance)
                .orElseThrow(() -> new IllegalStateException(
                        String.format("Balance not found for account %d. Account balance must be created when account is opened.", accountId)));
    }

    @Override
    @Transactional
    public BigDecimal getBalanceWithLock(Long accountId) {
        return accountBalanceRepository.findByAccountIdWithLock(accountId)
                .map(AccountBalance::getBalance)
                .orElseThrow(() -> new IllegalStateException(
                        String.format("Balance not found for account %d. Account balance must be created when account is opened.", accountId)));
    }
}
