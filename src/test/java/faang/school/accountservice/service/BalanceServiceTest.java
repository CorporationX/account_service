package faang.school.accountservice.service;

import faang.school.accountservice.dto.balance.BalanceDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.exception.EntityNotFoundException;
import faang.school.accountservice.mapper.BalanceMapperImpl;
import faang.school.accountservice.repository.BalanceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BalanceServiceTest {

    @Mock
    private BalanceRepository balanceRepository;

    @Mock
    private AccountService accountService;

    @Spy
    private BalanceMapperImpl balanceMapper;

    @Mock
    private BalanceAuditService balanceAuditService;

    @InjectMocks
    private BalanceService balanceService;

    @Test
    void getBalanceByIdTestEntityNotFoundException() {
        Long id = 1L;

        assertThrows(EntityNotFoundException.class, () -> balanceService.getBalanceById(id));
    }

    @Test
    void getBalanceByIdTest() {
        Long id = 1L;

        Balance balance = createBalanceWithId(id);
        balance.setActualBalance(BigDecimal.valueOf(0.));

        when(balanceRepository.findById(id)).thenReturn(Optional.of(balance));

        BalanceDto result = balanceService.getBalanceById(id);

        assertNotNull(result);
    }

    @Test
    void createBalanceTest() {
        Long id = 1L;
        Balance balance = new Balance();
        Account account = createAccountWithId(id);
        balance.setAccount(account);

        when(accountService.getAccountById(any())).thenReturn(account);

        BalanceDto result = balanceService.createBalance(id);

        verify(balanceRepository, times(1)).save(balance);
        assertEquals(id, result.accountId());
    }

    @Test
    void plusBalanceTest() {
        Long id = 1L;
        double money = 1.;
        long operationId = 1L;

        Balance balance = createBalanceWithId(id);

        when(balanceRepository.findByIdForUpdate(id)).thenReturn(Optional.of(balance));

        BalanceDto result = balanceService.plusBalance(id, money, operationId);

        assertEquals(BigDecimal.valueOf(money), result.actualBalance());
    }

    @Test
    void authBalanceTestException() {
        Long id = 1L;
        double money = 1.;
        long operationId = 1L;

        Balance balance = createBalanceWithId(id);
        balance.setAuthBalance(BigDecimal.valueOf(money));

        when(balanceRepository.findByIdForUpdate(id)).thenReturn(Optional.of(balance));

        assertThrows(IllegalArgumentException.class, () -> balanceService.authBalance(id, money, operationId));
    }

    @Test
    void authBalanceTest() {
        Long id = 1L;
        double money = 1.;
        long operationId = 1L;

        Balance balance = createBalanceWithId(id);
        balance.setActualBalance(BigDecimal.valueOf(money));

        when(balanceRepository.findByIdForUpdate(id)).thenReturn(Optional.of(balance));

        BalanceDto result = balanceService.authBalance(id, money, operationId);

        assertEquals(BigDecimal.valueOf(0.), result.actualBalance());
    }

    @Test
    void clearingBalanceAllSumTestException() {
        Long id = 1L;
        Balance balance = createBalanceWithId(id);
        long operationId = 1L;

        when(balanceRepository.findByIdForUpdate(id)).thenReturn(Optional.of(balance));

        assertThrows(IllegalArgumentException.class, () -> balanceService.clearingBalance(id, operationId));

    }

    @Test
    void clearingBalanceAllSumTest() {
        Long id = 1L;
        Balance balance = createBalanceWithId(id);
        balance.setAuthBalance(BigDecimal.valueOf(1.));
        long operationId = 1L;

        when(balanceRepository.findByIdForUpdate(id)).thenReturn(Optional.of(balance));

        BalanceDto result = balanceService.clearingBalance(id, operationId);

        assertEquals(BigDecimal.valueOf(0.), result.actualBalance());
    }

    @Test
    void clearingBalancePartSumTestException() {
        Long id = 1L;
        Double money = 1.;
        long operationId = 1L;

        Balance balance = createBalanceWithId(id);

        when(balanceRepository.findByIdForUpdate(id)).thenReturn(Optional.of(balance));

        assertThrows(IllegalArgumentException.class, () -> balanceService.clearingBalance(id, money, operationId));
    }

    @Test
    void clearingBalancePartSumTest() {
        Long id = 1L;
        Double money = 1.;
        long operationId = 1L;

        Balance balance = createBalanceWithId(id);
        balance.setAuthBalance(BigDecimal.valueOf(2.));

        when(balanceRepository.findByIdForUpdate(id)).thenReturn(Optional.of(balance));

        BalanceDto result = balanceService.clearingBalance(id, money, operationId);

        assertEquals(BigDecimal.valueOf(1.), result.actualBalance());
    }

    @Test
    void cancelBalanceTestException() {
        Long id = 1L;
        long operationId = 1L;

        Balance balance = createBalanceWithId(id);

        when(balanceRepository.findByIdForUpdate(id)).thenReturn(Optional.of(balance));

        assertThrows(IllegalArgumentException.class, () -> balanceService.cancelBalance(id, operationId));
    }

    @Test
    void cancelBalanceTest() {
        Long id = 1L;
        long operationId = 1L;

        Balance balance = createBalanceWithId(id);
        balance.setAuthBalance(BigDecimal.valueOf(1.));

        when(balanceRepository.findByIdForUpdate(id)).thenReturn(Optional.of(balance));

        BalanceDto result = balanceService.cancelBalance(id, operationId);

        assertEquals(BigDecimal.valueOf(1.), result.actualBalance());
    }

    private Balance createBalanceWithId(Long id) {
        return Balance.builder()
                .id(id)
                .account(createAccountWithId(id))
                .authBalance(BigDecimal.valueOf(0.))
                .actualBalance(BigDecimal.valueOf(0.))
                .build();
    }

    private Account createAccountWithId(Long id) {
        return Account.builder()
                .id(id)
                .build();
    }
}