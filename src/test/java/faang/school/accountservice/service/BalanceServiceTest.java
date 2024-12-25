package faang.school.accountservice.service;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.dto.TransactionDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.enums.OperationType;
import faang.school.accountservice.exception.BalanceBelowZeroException;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.repository.BalanceRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BalanceServiceTest {

    @Mock
    private BalanceRepository balanceRepository;

    @Spy
    private BalanceMapper balanceMapper = Mappers.getMapper(BalanceMapper.class);

    @InjectMocks
    private BalanceService balanceService;

    @Captor
    ArgumentCaptor<Balance> captor;

    @Test
    public void createBalanceTest() {
        Account account = new Account();
        account.setAccountNumber("123");

        balanceService.createBalance(account);

        verify(balanceRepository, times(1)).save(captor.capture());
        Balance balance = captor.getValue();
        assertEquals(BigDecimal.ZERO, balance.getActualBalance());
        assertEquals(BigDecimal.ZERO, balance.getAuthorizationBalance());
        assertEquals(account, balance.getAccount());
    }

    @Test
    public void getNonExistentBalanceTest() {
        Long accountId = 1L;
        when(balanceRepository.findByAccountId(accountId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> balanceService.getBalance(accountId));
    }

    @Test
    public void getBalanceTest() {
        Long accountId = 1L;
        Balance balance = new Balance();
        balance.setId(2L);
        BalanceDto expectedBalanceDto = balanceMapper.toDto(balance);
        when(balanceRepository.findByAccountId(accountId)).thenReturn(Optional.of(balance));

        BalanceDto receivedBalance  = balanceService.getBalance(accountId);

        assertEquals(expectedBalanceDto.getId(), receivedBalance.getId());
    }

    @Test
    public void updateBalanceToAmountBelowZeroOperationAuthorizationTest() {
        Long accountId = 1L;
        Balance balance = new Balance();
        balance.setId(2L);
        balance.setAuthorizationBalance(BigDecimal.valueOf(50));
        balance.setActualBalance(BigDecimal.valueOf(50));
        TransactionDto transactionDto = new TransactionDto(BigDecimal.valueOf(-100), OperationType.AUTHORIZATION);
        when(balanceRepository.findByAccountId(accountId)).thenReturn(Optional.of(balance));

        assertThrows(BalanceBelowZeroException.class, () -> balanceService.updateBalance(accountId, transactionDto));
    }

    @Test
    public void updateBalanceToAmountBelowZeroOperationClearingTest() {
        Long accountId = 1L;
        Balance balance = new Balance();
        balance.setId(2L);
        balance.setAuthorizationBalance(BigDecimal.valueOf(50));
        balance.setActualBalance(BigDecimal.valueOf(50));
        TransactionDto transactionDto = new TransactionDto(BigDecimal.valueOf(-100), OperationType.CLEARING);
        when(balanceRepository.findByAccountId(accountId)).thenReturn(Optional.of(balance));

        assertThrows(BalanceBelowZeroException.class, () -> balanceService.updateBalance(accountId, transactionDto));
    }

    @Test
    public void updateBalanceTest() {
        Long accountId = 1L;
        Balance balance = new Balance();
        balance.setId(2L);
        balance.setAuthorizationBalance(BigDecimal.valueOf(50));
        balance.setActualBalance(BigDecimal.valueOf(50));
        TransactionDto transactionDto = new TransactionDto(BigDecimal.valueOf(-10), OperationType.AUTHORIZATION);
        when(balanceRepository.findByAccountId(accountId)).thenReturn(Optional.of(balance));
        when(balanceRepository.save(balance)).thenReturn(balance);
        BalanceDto expectedBalanceDto = balanceMapper.toDto(balance);
        expectedBalanceDto.setAuthorizationBalance(BigDecimal.valueOf(40));
        expectedBalanceDto.setActualBalance(BigDecimal.valueOf(50));

        BalanceDto actualBalance = balanceService.updateBalance(accountId, transactionDto);

        assertEquals(expectedBalanceDto.getId(), actualBalance.getId());
        assertEquals(expectedBalanceDto.getActualBalance(), actualBalance.getActualBalance());
        assertEquals(expectedBalanceDto.getAuthorizationBalance(), actualBalance.getAuthorizationBalance());
    }
}
