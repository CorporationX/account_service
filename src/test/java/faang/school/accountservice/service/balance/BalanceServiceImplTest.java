package faang.school.accountservice.service.balance;

import faang.school.accountservice.enums.OperationType;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.Balance;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.BalanceRepository;
import faang.school.accountservice.service.balance.operation.Operation;
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
    private Map<OperationType, Operation> balanceChanges;

    @InjectMocks
    private BalanceServiceImpl balanceService;

    private Balance balance;
    private Account account;

    @BeforeEach
    public void setUp() {

        account = Account.builder()
                .id(1L)
                .build();

        balance = Balance.builder()
                .id(1L)
                .account(account)
                .actualBalance(BigDecimal.ZERO)
                .authBalance(BigDecimal.ZERO)
                .build();
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
}
