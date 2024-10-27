package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.balance.AmountChangeRequest;
import faang.school.accountservice.dto.balance.BalanceDto;
import faang.school.accountservice.enums.BalanceChangeType;
import faang.school.accountservice.exception.InsufficientFundsException;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.Balance;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.BalanceRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BalanceServiceImplTest {

    @Mock
    private BalanceRepository balanceRepository;

    @Spy
    private BalanceMapper balanceMapper = Mappers.getMapper(BalanceMapper.class);

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private Map<BalanceChangeType, BalanceChange> balanceChanges;

    @InjectMocks
    private BalanceServiceImpl balanceService;

    private BalanceChange replenishment;
    private BalanceChange withdrawal;
    private Balance balance;
    private Account account;
    private BalanceDto balanceDto;

    @BeforeEach
    public void setUp() {
        replenishment = new ReplenishmentChangeType();
        withdrawal = new WithdrawalChangeType();
        balanceChanges.put(replenishment.getChangeType(), replenishment);
        balanceChanges.put(withdrawal.getChangeType(), withdrawal);

        account = Account.builder()
                .id(1L)
                .build();

        balance = Balance.builder()
                .id(1L)
                .account(account)
                .actualBalance(BigDecimal.ZERO)
                .authBalance(BigDecimal.ZERO)
                .build();

        balanceDto = balanceMapper.toBalanceDto(balance);
    }

    @Test
    public void testRegisterBalanceChange() {
        balanceService.registerBalanceChange(replenishment.getChangeType(), replenishment);
        balanceService.registerBalanceChange(withdrawal.getChangeType(), withdrawal);

        verify(balanceChanges).put(replenishment.getChangeType(), replenishment);
        verify(balanceChanges).put(withdrawal.getChangeType(), withdrawal);
    }

    @Test
    public void testGetBalanceByAccountIdFail() {
        when(balanceRepository.findByAccountId(1L)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> balanceService.getBalanceByAccountId(1L));
    }

    @Test
    public void testGetBalanceByAccountIdSuccess() {
        when(balanceRepository.findByAccountId(1L)).thenReturn(Optional.of(balance));

        balanceService.getBalanceByAccountId(1L);

        verify(balanceRepository).findByAccountId(1L);
    }

    @Test
    public void testGetBalanceByIdFail() {
        when(balanceRepository.findById(1L)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> balanceService.getBalanceById(1L));
    }

    @Test
    public void testGetBalanceByIdSuccess() {
        when(balanceRepository.findById(1L)).thenReturn(Optional.of(balance));

        balanceService.getBalanceById(1L);

        verify(balanceRepository).findById(1L);
    }

    @Test
    public void testCreateBalanceFailWithNotFoundAccount() {
        when(accountRepository.findById(1L)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> balanceService.createBalance(1L));
    }

    @Test
    public void testCreateBalanceFailWithExistBalance() {
        account = Account.builder()
                .id(1L)
                .currentBalance(balance)
                .build();
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        assertThrows(EntityExistsException.class, () -> balanceService.createBalance(1L));
    }

    @Test
    public void testCreateBalanceSuccess() {
        account = Account.builder()
                .id(1L)
                .build();
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        balanceService.createBalance(1L);

        verify(balanceRepository).save(any(Balance.class));
    }

    @Test
    public void testChangeBalanceFailWithNotBalanceChangeType() {
        AmountChangeRequest amountChangeRequest = AmountChangeRequest.builder()
                .amount(BigDecimal.valueOf(10.0))
                .changeType(BalanceChangeType.WITHDRAWAL)
                .build();
        balanceService.registerBalanceChange(replenishment.getChangeType(), replenishment);
        when(balanceRepository.findById(1L)).thenReturn(Optional.of(balance));

        assertThrows(IllegalArgumentException.class,
                () -> balanceService.changeBalance(1L, amountChangeRequest));
    }

    @Test
    public void testChangeBalanceSuccess() {
        AmountChangeRequest amountChangeRequest = AmountChangeRequest.builder()
                .amount(BigDecimal.valueOf(10.0))
                .changeType(BalanceChangeType.REPLENISHMENT)
                .build();

        balanceService.registerBalanceChange(replenishment.getChangeType(), replenishment);
        when(balanceRepository.findById(1L)).thenReturn(Optional.of(balance));
        balanceDto = balanceService.changeBalance(1L, amountChangeRequest);

        verify(balanceRepository).save(any(Balance.class));
        assertEquals(BigDecimal.valueOf(10.0), balanceDto.actualBalance());
    }

    @Test
    public void testChangeBalanceFailWithNotEnoughBalance() {
        AmountChangeRequest amountChangeRequest = AmountChangeRequest.builder()
                .amount(BigDecimal.valueOf(10.0))
                .changeType(BalanceChangeType.WITHDRAWAL)
                .build();
        balanceService.registerBalanceChange(withdrawal.getChangeType(), withdrawal);
        when(balanceRepository.findById(1L)).thenReturn(Optional.of(balance));

        assertThrows(InsufficientFundsException.class,
                () -> balanceService.changeBalance(1L, amountChangeRequest));
    }

    @Test
    public void testChangeBalanceFailAuthBalanceMoreThanActualBalance() {
        balance = Balance.builder()
                .id(1L)
                .actualBalance(BigDecimal.valueOf(20))
                .authBalance(BigDecimal.valueOf(20))
                .build();
        AmountChangeRequest amountChangeRequest = AmountChangeRequest.builder()
                .amount(BigDecimal.valueOf(10.0))
                .changeType(BalanceChangeType.WITHDRAWAL)
                .build();

        balanceService.registerBalanceChange(withdrawal.getChangeType(), withdrawal);
        when(balanceRepository.findById(1L)).thenReturn(Optional.of(balance));

        assertThrows(InsufficientFundsException.class,
                () -> balanceService.changeBalance(1L, amountChangeRequest));
    }

    @Test
    public void testReserveBalanceFailWithNotEnoughBalance() {
        AmountChangeRequest amountChangeRequest = AmountChangeRequest.builder()
                .amount(BigDecimal.valueOf(10.0))
                .changeType(BalanceChangeType.REPLENISHMENT)
                .build();
        balanceService.registerBalanceChange(replenishment.getChangeType(), replenishment);
        when(balanceRepository.findById(1L)).thenReturn(Optional.of(balance));

        assertThrows(InsufficientFundsException.class,
                () -> balanceService.reserveBalance(1L, amountChangeRequest));
    }

    @Test
    public void testReserveBalanceSuccess() {
        balance = Balance.builder()
                .id(1L)
                .actualBalance(BigDecimal.valueOf(20))
                .authBalance(BigDecimal.ZERO)
                .build();

        AmountChangeRequest amountChangeRequest = AmountChangeRequest.builder()
                .amount(BigDecimal.valueOf(10.0))
                .changeType(BalanceChangeType.REPLENISHMENT)
                .build();
        balanceService.registerBalanceChange(replenishment.getChangeType(), replenishment);
        when(balanceRepository.findById(1L)).thenReturn(Optional.of(balance));
        balanceDto = balanceService.reserveBalance(1L, amountChangeRequest);

        verify(balanceRepository).save(any(Balance.class));
        assertEquals(BigDecimal.valueOf(10.0), balanceDto.authBalance());
    }
}
