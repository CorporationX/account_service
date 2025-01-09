package faang.school.accountservice.scheduler;

import faang.school.accountservice.enums.OperationStatus;
import faang.school.accountservice.model.account.Account;
import faang.school.accountservice.model.cashback.CashbackId;
import faang.school.accountservice.model.cashback.CashbackTariff;
import faang.school.accountservice.model.cashback.MerchantCashback;
import faang.school.accountservice.model.cashback.Operation;
import faang.school.accountservice.model.cashback.OperationCashback;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.cashback.MerchantCashbackRepository;
import faang.school.accountservice.repository.cashback.OperationCashbackRepository;
import faang.school.accountservice.repository.cashback.OperationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class CashbackScheduler {
    private final AccountRepository accountRepository;
    private final OperationCashbackRepository operationCashbackRepository;
    private final MerchantCashbackRepository merchantCashbackRepository;
    private final OperationRepository operationRepository;

    @Value("${cashback.processing.batch-size}")
    private int batchSize;

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void processDailyCashback() {
        log.info("Starting daily cashback processing");
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        processAccountsCashback(yesterday);
    }

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void processHourlyCashback() {
        log.info("Starting hourly cashback processing");
        LocalDateTime lastHour = LocalDateTime.now().minusHours(1);
        processAccountsCashback(lastHour);
    }

    private void processAccountsCashback(LocalDateTime from) {
        try {
            List<Account> accounts = accountRepository
                    .findAllWithCashbackTariff(PageRequest.of(0, batchSize));
            for (Account account : accounts) {
                try {
                    processAccountCashback(account, from);
                } catch (Exception e) {
                    log.error("Error processing account {}: {}", account.getId(), e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("Error in cashback processing: {}", e.getMessage());
        }
    }

    private void processAccountCashback(Account account, LocalDateTime from) {
        CashbackTariff tariff = account.getCashback_tariff();
        if (tariff == null) {
            log.debug("No cashback tariff for account {}", account.getId());
            return;
        }

        List<Operation> operations = operationRepository.findOperationByIdAndCreatedAtAfter(account.getId(), from);

        for (Operation operation : operations) {
            processSingleOperation(operation, tariff);
        }
    }

    private void processSingleOperation(Operation operation, CashbackTariff tariff) {
        Double cashbackPercentage = findCashbackPercentage(operation, tariff);
        if (cashbackPercentage == null || cashbackPercentage == 0) {
            return;
        }

        BigDecimal cashbackAmount = calculateCashback(operation.getAmount(), cashbackPercentage);
        saveCashbackOperation(operation, cashbackAmount);
    }

    private Double findCashbackPercentage(Operation operation, CashbackTariff tariff) {
        CashbackId operationCashbackId = new CashbackId(tariff.getId(), operation.getOperationTypeId());
        Optional<OperationCashback> operationCashback = operationCashbackRepository.findById(operationCashbackId);
        if (operationCashback.isPresent()) {
            return operationCashback.get().getPercentage();
        }
        CashbackId merchantCashbackId = new CashbackId(tariff.getId(), operation.getMerchantId());
        return merchantCashbackRepository.findById(merchantCashbackId)
                .map(MerchantCashback::getPercentage)
                .orElse(0.0);
    }

    private BigDecimal calculateCashback(BigDecimal amount, Double percentage) {
        return amount
                .multiply(BigDecimal.valueOf(percentage))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    public void processManually(LocalDateTime from, LocalDateTime to) {
        log.info("Starting manual cashback processing for period {} - {}", from, to);
        processAccountsCashback(from);
    }

    private void saveCashbackOperation(Operation operation, BigDecimal cashbackAmount) {
        Operation cashbackOperation = new Operation();
        cashbackOperation.setAmount(cashbackAmount);
        cashbackOperation.setOperationTypeId(2L);
        cashbackOperation.setStatus(OperationStatus.COMPLETED);
        cashbackOperation.setAccountId(operation.getAccountId());
        cashbackOperation.setOperationTypeId(operation.getOperationTypeId());
        operationRepository.save(cashbackOperation);
        operation.setCashbackProcessed(true);
        operation.setCashbackProcessedAt(LocalDateTime.now());
        operationRepository.save(operation);
    }
}