package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.balance.BalanceViewDto;
import faang.school.accountservice.exception.AccountNotFoundException;
import faang.school.accountservice.exception.BalanceOperationConflictException;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.model.Balance;
import faang.school.accountservice.repository.BalanceRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.Optional;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BalanceHelperTest {
    @Mock
    private BalanceRepository balanceRepository;

    @Mock
    private BalanceMapper balanceMapper;

    @InjectMocks
    private BalanceHelper balanceHelper;

    private final Long accountId = 1L;
    private final Balance balance = new Balance();
    private final BalanceViewDto expectedDto = new BalanceViewDto();

    @Test
    @DisplayName("Получение баланса, когда счет существует")
    public void givenExistingAccountId_whenGetBalance_thenReturnBalance() {
        when(balanceRepository.findByAccountId(accountId))
                .thenReturn(Optional.of(balance));

        Balance result = balanceHelper.getBalance(accountId);

        assertNotNull(result);
        assertEquals(balance, result);
        verify(balanceRepository).findByAccountId(accountId);
    }

    @Test
    @DisplayName("Получение баланса, когда счет не существует - должен выбросить исключение")
    public void givenNonExistingAccountId_whenGetBalance_thenThrowException() {
        when(balanceRepository.findByAccountId(accountId))
                .thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () ->
            balanceHelper.getBalance(accountId));
        verify(balanceRepository).findByAccountId(accountId);
    }

    @Test
    @DisplayName("Выполнение операции, когда нет конфликта, возвращает DTO")
    public void givenValidOperation_whenExecuteBalanceOperation_thenReturnDto() {
        Consumer<Balance> action = mock(Consumer.class);

        when(balanceRepository.findByAccountId(accountId))
                .thenReturn(Optional.of(balance));
        when(balanceMapper.toViewDto(balance))
                .thenReturn(expectedDto);

        BalanceViewDto result = balanceHelper.executeBalanceOperation(accountId, action);

        assertEquals(expectedDto, result);
        verify(action).accept(balance);
        verify(balanceMapper).toViewDto(balance);
    }

    @Test
    @DisplayName("При оптимистичной блокировке должен выбросить исключение конфликта")
    public void givenOptimisticLockConflict_whenExecuteBalanceOperation_thenThrowConflictException() {
        Consumer<Balance> action = mock(Consumer.class);

        when(balanceRepository.findByAccountId(accountId))
                .thenReturn(Optional.of(balance));
        doThrow(new ObjectOptimisticLockingFailureException("test", new Object()))
                .when(action).accept(balance);

        assertThrows(BalanceOperationConflictException.class, () -> {
            balanceHelper.executeBalanceOperation(accountId, action);
        });
    }
}
