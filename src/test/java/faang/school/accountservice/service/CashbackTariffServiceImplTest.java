package faang.school.accountservice.service;

import faang.school.accountservice.config.CashbackProperties;
import faang.school.accountservice.dto.CashbackBalanceDto;
import faang.school.accountservice.dto.CashbackTariffDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.entity.CashbackTariff;
import faang.school.accountservice.enums.OperationType;
import faang.school.accountservice.enums.RequestStatus;
import faang.school.accountservice.mapper.CashbackTariffMapper;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.BalanceAuditRepository;
import faang.school.accountservice.repository.CashbackTariffRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CashbackTariffServiceImplTest {

    @Mock
    private CashbackTariffRepository cashbackTariffRepository;

    @Mock
    private BalanceAuditRepository balanceAuditRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private BalanceService balanceService;

    @Mock
    private OperationTypeMappingService operationTypeMappingService;

    @Mock
    private MerchantMappingService merchantMappingService;

    @Mock
    private CashbackTariffMapper cashbackTariffMapper;

    @Spy
    private CashbackProperties cashbackProperties;

    @Mock
    private ExecutorService executorService;

    @InjectMocks
    private CashbackTariffServiceImpl cashbackTariffService;

    private CashbackTariffDto cashbackTariffDto;
    private CashbackTariff cashbackTariff;
    private Balance balance;
    private Account account;
    private CashbackBalanceDto cashbackBalanceDto;
    private long cashbackTariffId;
    private long balanceId;
    private long merchantId;

    @BeforeEach
    void setUp() {
        cashbackTariffId = 1L;
        balanceId = 1L;
        merchantId = 1L;
        averageBalance = new BigDecimal("100.00");
        cashbackTariffDto = CashbackTariffDto.builder()
                .id(cashbackTariffId)
                .name("Test Tariff")
                .build();
        cashbackTariff = CashbackTariff.builder()
                .id(cashbackTariffId)
                .name("Test Tariff")
                .operationTypeMappings(Collections.emptyList())
                .merchantMappings(Collections.emptyList())
                .build();
        cashbackProperties.setExpenditureOperations(List.of(
                OperationType.REFUND,
                OperationType.DEPOSIT
        ));
        balance = Balance.builder()
                .curFactBalance(new BigDecimal("50.00"))
                .id(balanceId)
                .build();
        account = Account.builder()
                .id(1L)
                .holderUserId(merchantId)
                .balance(balance)
                .cashbackTariff(cashbackTariff)
                .build();
        cashbackBalanceDto = CashbackBalanceDto.builder()
                .balanceId(balanceId)
                .averageBalance(averageBalance)
                .build();

        lenient().when(cashbackTariffMapper.toEntity(cashbackTariffDto)).thenReturn(cashbackTariff);
        lenient().when(cashbackTariffMapper.toDto(cashbackTariff)).thenReturn(cashbackTariffDto);
    }

    private BigDecimal averageBalance;

    @Test
    void createCashbackTariff_ShouldReturnDto_WhenValidInput() {
        when(cashbackTariffRepository.save(cashbackTariff)).thenReturn(cashbackTariff);

        CashbackTariffDto result = cashbackTariffService.createCashbackTariff(cashbackTariffDto);

        assertNotNull(result);
        assertEquals(cashbackTariffDto, result);
        verify(cashbackTariffMapper).toEntity(cashbackTariffDto);
        verify(cashbackTariffRepository).save(cashbackTariff);
        verify(cashbackTariffMapper).toDto(cashbackTariff);
    }

    @Test
    void getCashbackTariffById_ShouldReturnDto_WhenTariffExists() {
        when(cashbackTariffRepository.findById(cashbackTariffId)).thenReturn(java.util.Optional.of(cashbackTariff));
        when(cashbackTariffMapper.toDto(cashbackTariff)).thenReturn(cashbackTariffDto);

        CashbackTariffDto result = cashbackTariffService.getCashbackTariffById(cashbackTariffId);

        assertNotNull(result);
        assertEquals(cashbackTariffDto, result);
        ;
        verify(cashbackTariffRepository).findById(cashbackTariffId);
        verify(cashbackTariffMapper).toDto(cashbackTariff);
    }

    @Test
    void getCashbackTariffById_ShouldThrowException_WhenTariffDoesNotExist() {
        String correctMessage = "Cashback tariff with id %d not found".formatted(cashbackTariffId);
        when(cashbackTariffRepository.findById(cashbackTariffId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> cashbackTariffService.getCashbackTariffById(cashbackTariffId));

        assertEquals(correctMessage, exception.getMessage());
        verify(cashbackTariffRepository).findById(cashbackTariffId);
        verifyNoInteractions(cashbackTariffMapper);
    }

    @Test
    void earnCashbackOnExpensesAllAccounts_ShouldProcessAllBalances_WhenBalancesExist() {
        List<OperationType> expenditureOperations = cashbackProperties.getExpenditureOperations();
        List<CashbackBalanceDto> cashbackBalanceDtos = List.of(
                CashbackBalanceDto.builder().balanceId(1L).averageBalance(averageBalance).build(),
                CashbackBalanceDto.builder().balanceId(2L).averageBalance(averageBalance).build()
        );
        when(balanceAuditRepository.findFactAverageBalance(
                any(LocalDateTime.class),
                eq(expenditureOperations),
                eq(RequestStatus.COMPLETED)
        )).thenReturn(cashbackBalanceDtos);

        int result = cashbackTariffService.earnCashbackOnExpensesAllAccounts();

        assertEquals(2, result);
        verify(executorService, times(2)).execute(any(Runnable.class));
    }

    @Test
    void earnCashbackOnExpensesAllAccounts_ShouldReturnZero_WhenNoBalancesExist() {
        List<OperationType> expenditureOperations = cashbackProperties.getExpenditureOperations();
        when(balanceAuditRepository.findFactAverageBalance(
                any(LocalDateTime.class),
                eq(expenditureOperations),
                eq(RequestStatus.COMPLETED)
        )).thenReturn(Collections.emptyList());

        int result = cashbackTariffService.earnCashbackOnExpensesAllAccounts();

        assertEquals(0, result);
        verify(executorService, never()).execute(any(Runnable.class));
    }

    @Test
    void earnCashbackOnExpenses_ShouldUpdateBalance_WhenAccountAndTariffExist() {
        when(accountRepository.findAccountByBalanceIdWithCashbackTariff(balanceId)).thenReturn(Optional.of(account));
        when(operationTypeMappingService.applyCashbackToAmount(
                anyList(),
                eq(averageBalance)
        )).thenReturn(new BigDecimal("10.00"));
        when(merchantMappingService.applyCashbackToAmount(
                eq(merchantId),
                anyList(),
                eq(averageBalance)
        )).thenReturn(new BigDecimal("12.00"));

        cashbackTariffService.earnCashbackOnExpenses(cashbackBalanceDto);

        verify(balanceService).updateBalanceWithoutBalanceAudit(balanceId, new BigDecimal("62.00"));
    }

    @Test
    void earnCashbackOnExpenses_ShouldThrowException_WhenAccountNotFound() {
        String correctMessage = "Account with balance id %d not found".formatted(balanceId);
        cashbackBalanceDto.setAverageBalance(null);
        when(accountRepository.findAccountByBalanceIdWithCashbackTariff(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> cashbackTariffService.earnCashbackOnExpenses(cashbackBalanceDto));

        assertEquals(correctMessage, exception.getMessage());
    }

    @Test
    void earnCashbackOnExpenses_ShouldNotUpdate_WhenNoCashbackTariff() {
        account.setCashbackTariff(null);
        when(accountRepository.findAccountByBalanceIdWithCashbackTariff(balanceId)).thenReturn(Optional.of(account));

        cashbackTariffService.earnCashbackOnExpenses(cashbackBalanceDto);

        verify(balanceService, never()).updateBalanceWithoutBalanceAudit(anyLong(), any(BigDecimal.class));
    }

    @Test
    void earnCashbackOnExpenses_ShouldRetryOnOptimisticLockException() {
        BigDecimal newBalance = new BigDecimal("62.00");
        when(accountRepository.findAccountByBalanceIdWithCashbackTariff(balanceId)).thenReturn(Optional.of(account));
        when(operationTypeMappingService.applyCashbackToAmount(
                anyList(),
                any(BigDecimal.class)
        )).thenReturn(new BigDecimal("10.00"));
        when(merchantMappingService.applyCashbackToAmount(
                eq(merchantId),
                anyList(),
                eq(averageBalance)
        )).thenReturn(new BigDecimal("12.00"));
        doThrow(new OptimisticLockException()).when(balanceService).updateBalanceWithoutBalanceAudit(balanceId, newBalance);

        assertThrows(OptimisticLockException.class,
                () -> cashbackTariffService.earnCashbackOnExpenses(cashbackBalanceDto));

        verify(balanceService).updateBalanceWithoutBalanceAudit(balanceId, newBalance);
    }
}

