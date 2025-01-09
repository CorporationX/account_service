package faang.school.accountservice.scheduler;

import faang.school.accountservice.enums.OperationStatus;
import faang.school.accountservice.model.account.Account;
import faang.school.accountservice.model.cashback.CashbackTariff;
import faang.school.accountservice.model.cashback.Operation;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.cashback.OperationRepository;
import faang.school.accountservice.service.cashback.CashbackCalculationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CashbackScheduler {
    private final FailedOperationsService failedOperationsService;
    private final AccountRepository accountRepository;
    private final OperationRepository operationRepository;
    private final CashbackCalculationService cashbackCalculationService;

    @Value("${cashback.processing.batch-size}")
    private int batchSize;

    @Value("${cashback.retry.lookback-hours:24}")
    private int failedOperationsLookbackHours;

    @Scheduled(cron = "${cashback.retry.cron:0 */30 * * * *}")
    @Transactional
    public void processFailedOperations() {
        log.info("Starting failed operations processing");
        LocalDateTime lookbackTime = LocalDateTime.now().minusHours(failedOperationsLookbackHours);

        try {
            List<Operation> failedOperations = failedOperationsService.getFailedOperations(lookbackTime);
            log.info("Found {} failed operations to retry", failedOperations.size());

            for (Operation operation : failedOperations) {
                try {
                    failedOperationsService.retryFailedOperation(operation);
                } catch (Exception e) {
                    log.error("Error retrying operation {}: {}", operation.getId(), e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("Error during failed operations processing: {}", e.getMessage(), e);
        }
    }

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void processDailyCashback() {
        log.info("Starting daily cashback processing");
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        processCashbackBatch(yesterday);
    }

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void processHourlyCashback() {
        log.info("Starting hourly cashback processing");
        LocalDateTime lastHour = LocalDateTime.now().minusHours(1);
        processCashbackBatch(lastHour);
    }

    private void processCashbackBatch(LocalDateTime from) {
        log.info("Processing cashback for operations since: {}", from);
        try {
            List<Account> accounts = accountRepository
                    .findAllWithCashbackTariff(PageRequest.of(0, batchSize));
            log.info("Found {} accounts with cashback tariffs", accounts.size());

            for (Account account : accounts) {
                try {
                    processSingleAccountCashback(account, from);
                } catch (Exception e) {
                    log.error("Error processing account {}: {}", account.getId(), e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            log.error("Fatal error in cashback processing: {}", e.getMessage(), e);
            throw e;
        }
    }

    private void processSingleAccountCashback(Account account, LocalDateTime from) {
        CashbackTariff tariff = account.getCashback_tariff();
        if (tariff == null) {
            log.debug("No cashback tariff for account {}", account.getId());
            return;
        }

        List<Operation> operations = operationRepository.findUnprocessedOperationsByAccountAndDate(
                account.getId(),
                from
        );
        log.debug("Found {} operations for account {}", operations.size(), account.getId());

        for (Operation operation : operations) {
            if (!operation.getCashbackProcessed()) {
                processOperation(operation, account, tariff);
            }
        }
    }

    private void processOperation(Operation operation, Account account, CashbackTariff tariff) {
        try {
            BigDecimal cashbackAmount = cashbackCalculationService.calculateCashbackAmount(
                    operation,
                    tariff
            );

            if (cashbackAmount.compareTo(BigDecimal.ZERO) > 0) {
                createCashbackOperation(
                        operation,
                        account.getId(),
                        cashbackAmount
                );
            }

            operation.setCashbackProcessed(true);
            operation.setCashbackProcessedAt(LocalDateTime.now());
            operationRepository.save(operation);

            log.debug("Successfully processed cashback for operation {}", operation.getId());
        } catch (Exception e) {
            log.error("Failed to process operation {}: {}", operation.getId(), e.getMessage());
            operation.setRetryCount(operation.getRetryCount() + 1);
            operation.setLastRetryAt(LocalDateTime.now());
            operation.setErrorMessage(e.getMessage());
            operation.setStatus(OperationStatus.ERROR);
            operationRepository.save(operation);
        }
    }

    private void createCashbackOperation(Operation originalOperation, Long accountId, BigDecimal amount) {
        Operation cashbackOperation = new Operation();
        cashbackOperation.setAccountId(accountId);
        cashbackOperation.setAmount(amount);
        cashbackOperation.setStatus(OperationStatus.COMPLETED);
        cashbackOperation.setCashbackProcessed(true);
        cashbackOperation.setCreatedAt(LocalDateTime.now());
        operationRepository.save(cashbackOperation);
    }
}