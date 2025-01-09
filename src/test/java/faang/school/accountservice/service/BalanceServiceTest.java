package faang.school.accountservice.service;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.BalanceStatus;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.exception.BalanceInsufficientFundsException;
import faang.school.accountservice.exception.BalanceNotFoundException;
import faang.school.accountservice.mappers.BalanceMapper;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.Balance;
import faang.school.accountservice.repository.BalanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BalanceServiceTest {

    @Mock
    private BalanceRepository balanceRepository;

    @Mock
    private BalanceMapper balanceMapper;

    @InjectMocks
    private BalanceService balanceService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private BalanceDto createBalanceDto(Long accountId, BigDecimal authBalance, BigDecimal actualBalance) {
        return BalanceDto.builder()
                .id(accountId)
                .accountId(accountId)
                .authBalance(authBalance)
                .actualBalance(actualBalance)
                .build();
    }

    @Test
    void testGetBalanceByAccountId_Success() {
        Long accountId = 1L;
        Balance balance = new Balance();
        BalanceDto balanceDto = createBalanceDto(accountId, new BigDecimal("100.00"), new BigDecimal("100.00"));

        when(balanceRepository.findById(accountId)).thenReturn(Optional.of(balance));
        when(balanceMapper.toDto(balance)).thenReturn(balanceDto);

        BalanceDto result = balanceService.getBalanceByAccountId(accountId);

        assertNotNull(result);
        assertEquals(balanceDto, result);
        verify(balanceRepository, times(1)).findById(accountId);
    }

    @Test
    void testGetBalanceByAccountId_NotFound() {
        Long accountId = 1L;
        when(balanceRepository.findById(accountId)).thenReturn(Optional.empty());

        assertThrows(BalanceNotFoundException.class, () -> balanceService.getBalanceByAccountId(accountId));
        verify(balanceRepository, times(1)).findById(accountId);
    }

    @Test
    void testCreateBalance_Success() {
        Balance balance = new Balance();
        BalanceDto balanceDto = createBalanceDto(null, new BigDecimal("100.00"), new BigDecimal("100.00"));

        when(balanceMapper.toEntity(balanceDto)).thenReturn(balance);
        when(balanceRepository.save(balance)).thenReturn(balance);
        when(balanceMapper.toDto(balance)).thenReturn(balanceDto);

        BalanceDto result = balanceService.createBalance(balanceDto);

        assertNotNull(result);
        assertEquals(balanceDto, result);
        verify(balanceRepository, times(1)).save(balance);
    }

    @Test
    void testUpdateBalance_Success() {
        Long accountId = 1L;
        Balance balance = new Balance();
        BalanceDto balanceDto = createBalanceDto(accountId, new BigDecimal("200.00"), new BigDecimal("150.00"));

        when(balanceRepository.findById(accountId)).thenReturn(Optional.of(balance));
        when(balanceRepository.save(balance)).thenReturn(balance);
        when(balanceMapper.toDto(balance)).thenReturn(balanceDto);

        BalanceDto result = balanceService.updateBalance(accountId, balanceDto);

        assertNotNull(result);
        assertEquals(balanceDto, result);
        verify(balanceRepository, times(1)).save(balance);
    }

    @Test
    void testUpdateBalance_NotFound() {
        Long accountId = 1L;
        BalanceDto balanceDto = createBalanceDto(accountId, new BigDecimal("0.00"), new BigDecimal("0.00"));

        when(balanceRepository.findById(accountId)).thenReturn(Optional.empty());

        assertThrows(BalanceNotFoundException.class, () -> balanceService.updateBalance(accountId, balanceDto));
        verify(balanceRepository, times(1)).findById(accountId);
    }

    @Test
    public void testAuthorizePayment_Success() {
        Account account = new Account();
        account.setId(1L);

        Balance balance = new Balance();
        balance.setAuthBalance(BigDecimal.ZERO);
        balance.setStatus(BalanceStatus.APPROVED);
        account.setBalance(balance);

        BigDecimal amount = new BigDecimal("100.00");

        balanceService.authorizePayment(account, amount);

        assertEquals(new BigDecimal("100.00"), balance.getAuthBalance());
        assertEquals(BalanceStatus.PENDING, balance.getStatus());

        verify(balanceRepository, times(1)).save(balance);
    }

    @Test
    public void testAuthorizePayment_NullAccount_ThrowsException() {
        BigDecimal amount = new BigDecimal("100.00");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            balanceService.authorizePayment(null, amount);
        });

        assertEquals("Account or amount cannot be null", exception.getMessage());
    }

    @Test
    public void testAuthorizePayment_NullAmount_ThrowsException() {
        Account account = new Account();
        account.setId(1L);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            balanceService.authorizePayment(account, null);
        });

        assertEquals("Account or amount cannot be null", exception.getMessage());
    }

    @Test
    public void testAuthorizePayment_NullBalance_ThrowsException() {
        Account account = new Account();
        account.setId(1L);
        account.setBalance(null);

        BigDecimal amount = new BigDecimal("100.00");

        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            balanceService.authorizePayment(account, amount);
        });

        assertEquals(NullPointerException.class, exception.getClass());
    }

    @Test
    public void testClearingPayment_Success() {
        Long senderBalanceId = 1L;
        Long recipientBalanceId = 2L;
        BigDecimal amount = new BigDecimal("100.00");

        Balance senderBalance = new Balance();
        senderBalance.setId(senderBalanceId);
        senderBalance.setActualBalance(new BigDecimal("500.00"));
        senderBalance.setAuthBalance(new BigDecimal("200.00"));
        senderBalance.setStatus(BalanceStatus.PENDING);

        Balance recipientBalance = new Balance();
        recipientBalance.setId(recipientBalanceId);
        recipientBalance.setActualBalance(new BigDecimal("300.00"));
        recipientBalance.setStatus(BalanceStatus.PENDING);

        when(balanceRepository.findById(senderBalanceId)).thenReturn(Optional.of(senderBalance));
        when(balanceRepository.findById(recipientBalanceId)).thenReturn(Optional.of(recipientBalance));

        balanceService.clearingPayment(senderBalanceId, recipientBalanceId, amount);

        assertEquals(new BigDecimal("400.00"), recipientBalance.getActualBalance());
        assertEquals(BalanceStatus.APPROVED, recipientBalance.getStatus());

        assertEquals(new BigDecimal("400.00"), senderBalance.getActualBalance());
        assertEquals(new BigDecimal("100.00"), senderBalance.getAuthBalance());
        assertEquals(BalanceStatus.APPROVED, senderBalance.getStatus());

        verify(balanceRepository, times(1)).save(recipientBalance);
        verify(balanceRepository, times(1)).save(senderBalance);
    }

    @Test
    public void testClearingPayment_SenderBalanceNotFound_ThrowsException() {
        Long senderBalanceId = 1L;
        Long recipientBalanceId = 2L;
        BigDecimal amount = new BigDecimal("100.00");

        when(balanceRepository.findById(senderBalanceId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            balanceService.clearingPayment(senderBalanceId, recipientBalanceId, amount);
        });

        assertEquals("Sender balance not found", exception.getMessage());
    }

    @Test
    public void testClearingPayment_RecipientBalanceNotFound_ThrowsException() {
        Long senderBalanceId = 1L;
        Long recipientBalanceId = 2L;
        BigDecimal amount = new BigDecimal("100.00");

        Balance senderBalance = new Balance();
        senderBalance.setId(senderBalanceId);
        senderBalance.setActualBalance(new BigDecimal("500.00"));
        senderBalance.setAuthBalance(new BigDecimal("200.00"));
        senderBalance.setStatus(BalanceStatus.PENDING);


        when(balanceRepository.findById(senderBalanceId)).thenReturn(Optional.of(senderBalance));
        when(balanceRepository.findById(recipientBalanceId)).thenReturn(Optional.empty());


        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            balanceService.clearingPayment(senderBalanceId, recipientBalanceId, amount);
        });

        assertEquals("Recipient balance not found", exception.getMessage());
    }

    @Test
    void testAuthorizePayment_AccountEqualsNull() {
        Account account = new Account();
    }
}