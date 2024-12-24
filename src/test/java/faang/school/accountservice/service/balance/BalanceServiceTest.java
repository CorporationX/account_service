package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.balance.ResponseBalanceDto;
import faang.school.accountservice.exception.account.AccountNotFoundException;
import faang.school.accountservice.exception.balance.BalanceHasBeenUpdatedException;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.model.account.Account;
import faang.school.accountservice.model.balance.Balance;
import faang.school.accountservice.repository.BalanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.OptimisticLockingFailureException;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BalanceServiceTest {

    @Mock
    private BalanceRepository balanceRepository;

    @Mock
    private BalanceMapper balanceMapper;

    @InjectMocks
    private BalanceService balanceService;

    private Account account;
    private Balance balance;
    private ResponseBalanceDto responseBalanceDto;

    @BeforeEach
    public void setUp() {
        account = Account.builder()
                .id(1L)
                .build();

        balance = Balance.builder()
                .id(100L)
                .authorizationBalance(new BigDecimal("500.00"))
                .actualBalance(new BigDecimal("1000.00"))
                .account(account)
                .build();

        responseBalanceDto = ResponseBalanceDto.builder()
                .id(balance.getId())
                .authorizationBalance(balance.getAuthorizationBalance())
                .actualBalance(balance.getActualBalance())
                .build();
    }

    @Test
    @DisplayName("getBalance - Success")
    public void getBalance_Success() {
        Long accountId = account.getId();
        when(balanceRepository.findByAccountId(accountId)).thenReturn(Optional.of(balance));
        when(balanceMapper.toDto(balance)).thenReturn(responseBalanceDto);

        ResponseBalanceDto result = balanceService.getBalance(accountId);

        assertNotNull(result);
        assertEquals(responseBalanceDto.getId(), result.getId());
        assertEquals(responseBalanceDto.getAuthorizationBalance(), result.getAuthorizationBalance());
        assertEquals(responseBalanceDto.getActualBalance(), result.getActualBalance());

        verify(balanceRepository, times(1)).findByAccountId(accountId);
        verify(balanceMapper, times(1)).toDto(balance);
    }

    @Test
    @DisplayName("getBalance - Account Not Found")
    public void getBalance_AccountNotFound() {
        Long invalidAccountId = 2L;
        when(balanceRepository.findByAccountId(invalidAccountId)).thenReturn(Optional.empty());

        AccountNotFoundException exception = assertThrows(AccountNotFoundException.class, () ->
                balanceService.getBalance(invalidAccountId));

        assertEquals("Account not found", exception.getMessage());

        verify(balanceRepository, times(1)).findByAccountId(invalidAccountId);
        verify(balanceMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("createBalance - Success")
    public void createBalance_Success() {
        when(balanceRepository.saveAndFlush(any(Balance.class))).thenReturn(balance);

        Balance createdBalance = balanceService.createBalance(account);

        assertNotNull(createdBalance);
        assertEquals(balance.getId(), createdBalance.getId());
        assertEquals(balance.getAuthorizationBalance(), createdBalance.getAuthorizationBalance());
        assertEquals(balance.getActualBalance(), createdBalance.getActualBalance());
        assertEquals(account, createdBalance.getAccount());

        verify(balanceRepository, times(1)).saveAndFlush(any(Balance.class));
    }

    @Test
    @DisplayName("createBalance - Optimistic Locking Failure")
    public void createBalance_OptimisticLockingFailure() {
        when(balanceRepository.saveAndFlush(any(Balance.class))).thenThrow(OptimisticLockingFailureException.class);

        assertThrows(BalanceHasBeenUpdatedException.class, () -> balanceService.createBalance(account));

        verify(balanceRepository, times(1)).saveAndFlush(any(Balance.class));
    }
}
