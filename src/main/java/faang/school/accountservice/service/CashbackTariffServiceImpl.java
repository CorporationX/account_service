package faang.school.accountservice.service;

import faang.school.accountservice.config.CashbackProperties;
import faang.school.accountservice.dto.CashbackBalanceDto;
import faang.school.accountservice.dto.CashbackTariffDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.CashbackTariff;
import faang.school.accountservice.enums.RequestStatus;
import faang.school.accountservice.mapper.CashbackTariffMapper;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.BalanceAuditRepository;
import faang.school.accountservice.repository.CashbackTariffRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;

@Service
@RequiredArgsConstructor
public class CashbackTariffServiceImpl implements CashbackTariffService {

    private final CashbackTariffRepository cashbackTariffRepository;
    private final BalanceAuditRepository balanceAuditRepository;
    private final AccountRepository accountRepository;
    private final BalanceService balanceService;
    private final OperationTypeMappingService operationTypeMappingService;
    private final MerchantMappingService merchantMappingService;
    private final CashbackTariffMapper cashbackTariffMapper;
    private final CashbackProperties cashbackProperties;
    private final ExecutorService executorService;

    @Override
    @Transactional
    public CashbackTariffDto createCashbackTariff(CashbackTariffDto cashbackTariffDto) {
        CashbackTariff cashbackTariff = cashbackTariffMapper.toEntity(cashbackTariffDto);
        cashbackTariff = cashbackTariffRepository.save(cashbackTariff);
        return cashbackTariffMapper.toDto(cashbackTariff);
    }

    @Override
    public CashbackTariffDto getCashbackTariffById(long id) {
        return cashbackTariffRepository.findById(id)
                .map(cashbackTariffMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Cashback tariff with id %d not found".formatted(id)));
    }

    @Override
    public int earnCashbackOnExpensesAllAccounts() {
        List<CashbackBalanceDto> cashbackBalanceDtos = balanceAuditRepository.findFactAverageBalance(
                LocalDateTime.now().minusMonths(1L),
                cashbackProperties.getExpenditureOperations(),
                RequestStatus.COMPLETED
        );
        cashbackBalanceDtos.forEach(cashbackBalanceDto -> executorService.execute(
                () -> earnCashbackOnExpenses(cashbackBalanceDto))
        );

        return cashbackBalanceDtos.size();
    }

    @Override
    @Retryable(retryFor = OptimisticLockException.class,
            maxAttemptsExpression = "${cashback.max-attempts}",
            backoff = @Backoff(delayExpression = "${cashback.backoff-delay}")
    )
    public void earnCashbackOnExpenses(CashbackBalanceDto cashbackBalanceDto) {
        Long balanceId = cashbackBalanceDto.getBalanceId();
        Account account = accountRepository.findAccountByBalanceIdWithCashbackTariff(balanceId)
                .orElseThrow(() -> new EntityNotFoundException("Account with balance id %d not found".formatted(balanceId)));
        CashbackTariff cashbackTariff = account.getCashbackTariff();

        if (cashbackTariff != null) {
            BigDecimal newBalance = applyCashbackTariff(
                    account.getHolderUserId(),
                    cashbackTariff,
                    account.getBalance().getCurFactBalance(),
                    cashbackBalanceDto.getAverageBalance()
            );
            balanceService.updateBalanceWithoutBalanceAudit(balanceId, newBalance);
        }
    }

    private BigDecimal applyCashbackTariff(Long merchantId, CashbackTariff cashbackTariff, BigDecimal balance, BigDecimal averageBalance) {
        var operationTypeMappings = cashbackTariff.getOperationTypeMappings();
        BigDecimal first = operationTypeMappingService.applyCashbackToAmount(operationTypeMappings, averageBalance);

        if (merchantId != null) {
            var merchantMappings = cashbackTariff.getMerchantMappings();
            BigDecimal second = merchantMappingService.applyCashbackToAmount(merchantId, merchantMappings, averageBalance);
            BigDecimal maxCashback = first.max(second);
            return balance.add(maxCashback);
        } else {
            return first;
        }
    }
}
