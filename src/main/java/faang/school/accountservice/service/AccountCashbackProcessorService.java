package faang.school.accountservice.service;

import faang.school.accountservice.entity.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Set;

@Service
public class AccountCashbackProcessorService {
    // TODO Fix this moment with List<BalanceAudits>
    @Transactional
    public void processAccountCashback(Account account) {
        BigDecimal totalCashbackPercentage = calculateTotalCashbackPercentage(account);

        if (totalCashbackPercentage.compareTo(BigDecimal.ZERO) > 0) {
            applyCashback(account, totalCashbackPercentage);
        }
    }

    private BigDecimal calculateTotalCashbackPercentage(Account account) {
        CashbackTariff cashbackTariff = account.getCashbackTariff();
        Set<CashbackOperationMapping> cashbackOperationMappings = cashbackTariff.getOperationMappings();
        Set<CashbackMerchantMapping> cashbackMerchantMappings = cashbackTariff.getMerchantMappings();

        BigDecimal operationCashback = cashbackOperationMappings.stream()
            .map(CashbackOperationMapping::getPercentage)
            .max(BigDecimal::compareTo)
            .orElse(BigDecimal.ZERO);

        BigDecimal merchantCashback = cashbackMerchantMappings.stream()
            .map(CashbackMerchantMapping::getPercentage)
            .max(BigDecimal::compareTo)
            .orElse(BigDecimal.ZERO);

        return operationCashback.max(merchantCashback);
    }

    private void applyCashback(Account account, BigDecimal totalCashbackPercentage) {
        BigDecimal currentBalance = account.getBalance().getCurrentBalance();
        BigDecimal cashbackAmount = currentBalance.multiply(totalCashbackPercentage);
        BigDecimal newBalance = currentBalance.add(cashbackAmount);

        account.getBalance().setCurrentBalance(newBalance);
    }
}
