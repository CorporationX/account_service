package faang.school.accountservice.service.savings_account;

import faang.school.accountservice.dto.TransactionDto;
import faang.school.accountservice.entity.savings_account.SavingsAccount;
import faang.school.accountservice.enums.OperationType;
import faang.school.accountservice.repository.savings_account.SavingsAccountRepository;
import faang.school.accountservice.service.BalanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncSavingsAccountService {

    private static final BigDecimal ONE_HUNDRED_PERCENT = new BigDecimal("100");

    private final SavingsAccountRepository savingsAccountRepository;
    private final BalanceService balanceService;

    @Transactional
    @Async("taskExecutor")
    public CompletableFuture<Void> accrueInterest(List<SavingsAccount> savingsAccounts) {
        int daysInYear = LocalDate.now().withDayOfYear(1).lengthOfYear();
        List<SavingsAccount> updatedAccounts = new ArrayList<>();

        savingsAccounts.forEach(savingsAccount -> {
            log.info("Debugging {} account", savingsAccount.getId());
            BigDecimal currentBalance = getActualBalance(savingsAccount);
            BigDecimal amountToAdd = calculatePercentFromValue(
                    currentBalance,
                    savingsAccount.getTariff().getCurrentRate()
                            .divide(new BigDecimal(daysInYear), MathContext.DECIMAL64)
            );
            TransactionDto transactionDto = TransactionDto.builder()
                    .operationId(1L)
                    .amount(amountToAdd)
                    .operationType(OperationType.CLEARING)
                    .build();
            balanceService.updateBalance(savingsAccount.getAccount().getId(), transactionDto);
            savingsAccount.setLastInterestDate(LocalDateTime.now());
            updatedAccounts.add(savingsAccount);
        });

        savingsAccountRepository.saveAll(updatedAccounts);
        return CompletableFuture.completedFuture(null);
    }

    private BigDecimal getActualBalance(SavingsAccount savingsAccount) {
        return balanceService
                .getBalance(savingsAccount
                        .getAccount()
                        .getId())
                .getActualBalance();
    }

    private BigDecimal calculatePercentFromValue(BigDecimal value, BigDecimal percent) {
        return value.multiply(percent).divide(ONE_HUNDRED_PERCENT, MathContext.DECIMAL64);
    }
}
