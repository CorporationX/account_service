package faang.school.accountservice.service;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.BalanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigInteger;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BalanceServiceTest {
    @Mock
    private BalanceRepository balanceRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private BalanceMapper balanceMapper;

    private BalanceService balanceService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        balanceService = new BalanceService(balanceRepository, accountRepository, balanceMapper);
    }

    @Test
    public void testSaveBalance() {
        // Arrange
        BalanceDto balanceDto = new BalanceDto();
        balanceDto.setAccountId(1L);
        Balance balance = new Balance();
        balance.setCurrBalance(BigInteger.valueOf(1000));

        when(balanceMapper.toBalance(balanceDto)).thenReturn(balance);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(new Account()));
        when(balanceRepository.save(balance)).thenReturn(balance);
        when(balanceMapper.toDto(balance)).thenReturn(balanceDto);

        // Act
        BalanceDto savedBalanceDto = balanceService.saveBalance(balanceDto);

        // Assert
        assertNotNull(savedBalanceDto);
        verify(balanceMapper).toBalance(balanceDto);
        verify(accountRepository).findById(1L);
        verify(balanceRepository).save(balance);
        verify(balanceMapper).toDto(balance);
    }

    @Test
    public void testSaveBalanceThrowsNullPointerException() {
        // Act & Assert
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            balanceService.saveBalance(null);
        });
        assertEquals("Balance is null", exception.getMessage());
    }

    @Test
    public void testUpdateBalance() {
        // Arrange
        long accountId = 1L;
        BigInteger authBalance = BigInteger.valueOf(500);
        Balance balance = new Balance();
        balance.setCurrBalance(BigInteger.valueOf(1000));

        when(balanceRepository.findByAccountId(accountId)).thenReturn(balance);

        // Act
        balanceService.updateBalance(accountId, authBalance);

        // Assert
        assertEquals(BigInteger.valueOf(500), balance.getCurrBalance());
        verify(balanceRepository).findByAccountId(accountId);
        verify(balanceRepository).updateByAccountId(accountId, BigInteger.valueOf(500));
    }

    @Test
    public void testUpdateBalanceThrowsIllegalArgumentException() {
        // Arrange
        long accountId = 1L;
        BigInteger authBalance = BigInteger.valueOf(1500);
        Balance balance = new Balance();
        balance.setCurrBalance(BigInteger.valueOf(1000));

        when(balanceRepository.findByAccountId(accountId)).thenReturn(balance);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            balanceService.updateBalance(accountId, authBalance);
        });
        assertEquals("Current balance is less than auth balance", exception.getMessage());
    }

    @Test
    public void testWriteOff() {
        // Arrange
        Balance balance = new Balance();
        balance.setCurrBalance(BigInteger.valueOf(1000));
        BigInteger authBalance = BigInteger.valueOf(500);

        // Act
        balanceService.writeOff(balance, authBalance);

        // Assert
        assertEquals(BigInteger.valueOf(500), balance.getCurrBalance());
    }

    @Test
    public void testWriteOffThrowsIllegalArgumentException() {
        // Arrange
        Balance balance = new Balance();
        balance.setCurrBalance(BigInteger.valueOf(300));
        BigInteger authBalance = BigInteger.valueOf(500);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            balanceService.writeOff(balance, authBalance);
        });
        assertEquals("Current balance is less than auth balance", exception.getMessage());
    }
}
