package faang.school.accountservice.service;

import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.CashbackMerchantMapping;
import faang.school.accountservice.model.CashbackOperationMapping;
import faang.school.accountservice.model.Transaction;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.CashbackMerchantMappingRepository;
import faang.school.accountservice.repository.CashbackOperationMappingRepository;
import faang.school.accountservice.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CashbackJobService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final CashbackOperationMappingRepository operationMappingRepository;
    private final CashbackMerchantMappingRepository merchantMappingRepository;

    @Scheduled(cron = "0 0 0 1 * ?")
    public void calculateMonthlyCashbackForAllAccounts() {
        List<Account> accounts = accountRepository.findAll();
        accounts.parallelStream().forEach(this::calculateMonthlyCashbackForAccount);
    }

    private void calculateMonthlyCashbackForAccount(Account account) {
        Long tariffId = account.getTariffId();
        Long accountId = account.getId();

        List<Transaction> transactions = transactionRepository
                .findExpenseTransactionsForAccountAndTariff(accountId, tariffId,
                        LocalDate.now().minusMonths(1), LocalDate.now());

        BigDecimal totalCashback = transactions.stream()
                .map(transaction -> calculateMonthlyCashbackForTransaction(tariffId, transaction))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        account.setCashbackBalance(account.getCashbackBalance().add(totalCashback));
        accountRepository.save(account);
    }

    private BigDecimal calculateMonthlyCashbackForTransaction(Long tariffId, Transaction transaction) {
        BigDecimal operationPercent = operationMappingRepository
                .findByTariffIdAndOperationType(tariffId, transaction.getOperationType())
                .map(CashbackOperationMapping::getCashbackPercent)
                .orElse(BigDecimal.ZERO);

        BigDecimal merchantPercent = merchantMappingRepository
                .findByTariffIdAndMerchant(tariffId, transaction.getMerchant())
                .map(CashbackMerchantMapping::getCashbackPercent)
                .orElse(BigDecimal.ZERO);

        BigDecimal cashbackPercent = operationPercent.max(merchantPercent);

        return transaction.getAmount().multiply(cashbackPercent).divide(BigDecimal.valueOf(100));
    }
}