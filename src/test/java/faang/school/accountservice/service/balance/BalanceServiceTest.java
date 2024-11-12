package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.balance.BalanceDto;
import faang.school.accountservice.dto.balance.TransactionDto;
import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.entity.balance.Balance;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.TransactionType;
import faang.school.accountservice.exception.BalanceException;
import faang.school.accountservice.mapper.balance.BalanceMapper;
import faang.school.accountservice.repository.account.AccountRepository;
import faang.school.accountservice.repository.balance.BalanceRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BalanceServiceTest {
    private TransactionDto transactionDto;
    private Account account;
    private Balance balance;
    private BalanceDto balanceDto;
    private long accountId = 1;

    @InjectMocks
    private BalanceService balanceService;

    @Mock
    private BalanceRepository balanceRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private BalanceMapper balanceMapper;

    @Mock
    private TransactionService transactionService;

    @BeforeEach
    void init() {

        transactionDto = TransactionDto.builder()
                .paymentNumber(12345L)
                .transactionType(TransactionType.CREDITING)
                .amount(BigDecimal.valueOf(100))
                .currency(Currency.RUB)
                .build();

        balance = Balance.builder()
                .id(1)
                .authorizationBalance(BigDecimal.valueOf(100.00))
                .actualBalance(BigDecimal.valueOf(150.00))
                .paymentNumber(12345L)
                .createdAt(LocalDateTime.of(2023, 10, 1, 12, 0))
                .updatedAt(LocalDateTime.of(2023, 10, 5, 12, 0))
                .version(1)
                .build();

        balanceDto = BalanceDto.builder()
                .id(1)
                .authorizationBalance(BigDecimal.valueOf(100.00))
                .actualBalance(BigDecimal.valueOf(150.00))
                .createdAt(LocalDateTime.of(2023, 10, 1, 12, 0))
                .updatedAt(LocalDateTime.of(2023, 10, 5, 12, 0))
                .version(1)
                .build();

        account = Account.builder()
                .id(1)
                .number("123456789012")
                .currency(Currency.RUB)
                .status(AccountStatus.ACTIVE)
                .balance(balance)
                .build();
    }

    @Test
    @DisplayName("successful execution of method Create")
    void testCreate() {

        Balance balanceToSave = Balance.builder()
                .actualBalance(BigDecimal.ZERO)
                .account(account)
                .build();

        when(balanceRepository.save(any(Balance.class))).thenReturn(balanceToSave);

        balanceService.create(account);

        verify(balanceRepository, times(1)).save(any(Balance.class));

        ArgumentCaptor<Balance> balanceCaptor = ArgumentCaptor.forClass(Balance.class);
        verify(balanceRepository).save(balanceCaptor.capture());

        Balance savedBalance = balanceCaptor.getValue();
        assertEquals(BigDecimal.ZERO, savedBalance.getActualBalance(), "Actual balance should be zero");
        assertEquals(account, savedBalance.getAccount(), "Account should match the provided account");
    }

    @Test
    @DisplayName("successful execution of method GetBalance")
    void testGetBalance() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(balanceMapper.toBalanceDto(account.getBalance())).thenReturn(balanceDto);

        BalanceDto result = balanceService.getBalance(accountId);

        verify(accountRepository).findById(accountId);
        verify(balanceMapper).toBalanceDto(account.getBalance());

        assertEquals(result.getAccountId(), account.getId());
    }

    @Test
    void testUpdate_success_whenArgsValid() {
        Balance newBalance = mock(Balance.class);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(transactionService.processTransaction(transactionDto, account)).thenReturn(newBalance);
        when(balanceRepository.save(newBalance)).thenReturn(newBalance);
        when(balanceMapper.toBalanceDto(newBalance)).thenReturn(balanceDto);

        BalanceDto result = balanceService.update(accountId, transactionDto);

        assertNotNull(result);
        assertEquals(balanceDto, result);

        verify(accountRepository, times(1)).findById(accountId);
        verify(transactionService, times(1)).processTransaction(transactionDto, account);
        verify(balanceRepository, times(1)).save(newBalance);
        verify(balanceMapper, times(1)).toBalanceDto(newBalance);
    }

    @Test
    void testUpdate_throwsEntityNotFoundException_whenAccountNotFound() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> balanceService.update(accountId, transactionDto));

        verify(accountRepository, times(1)).findById(accountId);
        verifyNoMoreInteractions(transactionService, balanceRepository, balanceMapper);
    }

    @Test
    void testUpdate_throwsBalanceException_whenCurrencyMismatch() {
        account.setCurrency(Currency.USD);
        transactionDto.setCurrency(Currency.EUR);
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        assertThrows(BalanceException.class, () -> balanceService.update(accountId, transactionDto));

        verify(accountRepository, times(1)).findById(accountId);
        verifyNoMoreInteractions(transactionService, balanceRepository, balanceMapper);
    }
}
