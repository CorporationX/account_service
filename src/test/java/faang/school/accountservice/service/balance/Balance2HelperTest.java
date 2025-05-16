package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.balance.BalanceViewDto;
import faang.school.accountservice.exception.BalanceOperationConflictException;
import faang.school.accountservice.mapper.BalanceMapper2;
import faang.school.accountservice.model.Balance2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class Balance2HelperTest {

    @Mock
    private BalanceService2 balanceService2;

    @Mock
    private BalanceMapper2 balanceMapper;

    @InjectMocks
    private BalanceHelper balanceHelper;

    private final Long accountId = 1L;
    private final Balance2 balance2 = new Balance2();
    private final BalanceViewDto expectedDto = new BalanceViewDto();

    @Test
    @DisplayName("Выполнение операции, когда нет конфликта, возвращает DTO")
    public void givenValidOperation_whenExecuteBalanceOperation_thenReturnDto() {
        Consumer<Balance2> action = mock(Consumer.class);

        when(balanceService2.getBalanceEntity(accountId)).thenReturn(balance2);
        when(balanceMapper.toViewDto(balance2)).thenReturn(expectedDto);

        BalanceViewDto result = balanceHelper.executeBalanceOperation(accountId, action);

        assertEquals(expectedDto, result);
        verify(action).accept(balance2);
        verify(balanceMapper).toViewDto(balance2);
    }

    @Test
    @DisplayName("При оптимистичной блокировке должен выбросить исключение конфликта")
    public void givenOptimisticLockConflict_whenExecuteBalanceOperation_thenThrowConflictException() {
        Consumer<Balance2> action = mock(Consumer.class);

        when(balanceService2.getBalanceEntity(accountId)).thenReturn(balance2);
        doThrow(new ObjectOptimisticLockingFailureException("test", new Object()))
                .when(action).accept(balance2);

        assertThrows(BalanceOperationConflictException.class, () -> {
            balanceHelper.executeBalanceOperation(accountId, action);
        });
    }
}
