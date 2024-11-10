package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.balance.AmountChangeRequest;
import faang.school.accountservice.dto.balance.BalanceDto;
import faang.school.accountservice.enums.ChangeBalanceType;
import faang.school.accountservice.enums.OperationType;
import faang.school.accountservice.exception.InsufficientFundsException;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.Balance;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.BalanceRepository;
import faang.school.accountservice.service.balance.changebalance.ActualBalanceChanger;
import faang.school.accountservice.service.balance.changebalance.AuthorizationBalanceChanger;
import faang.school.accountservice.service.balance.changebalance.BalanceChangeRegistry;
import faang.school.accountservice.service.balance.operation.Operation;
import faang.school.accountservice.service.balance.operation.OperationRegistry;
import faang.school.accountservice.service.balance.operation.ReplenishmentOperation;
import faang.school.accountservice.service.balance.operation.WithdrawalOperation;
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
import java.util.HashMap;
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
    private OperationRegistry operationRegistry;

    @Mock
    private BalanceChangeRegistry changeRegistry;

    private Map<OperationType, Operation> operations;

    @InjectMocks
    private BalanceServiceImpl balanceService;

    private Balance balance;
    private Account account;
    private BalanceDto balanceDto;

    @BeforeEach
    public void setUp() {

        account = Account.builder()
                .id(1L)
                .build();

        balance = Balance.builder()
                .id(1L)
                .account(account)
                .actualBalance(BigDecimal.valueOf(100))
                .authBalance(BigDecimal.valueOf(100))
                .build();

        balanceDto = balanceMapper.toBalanceDto(balance);

        operations = new HashMap<>();
        operations.put(OperationType.REPLENISHMENT, new ReplenishmentOperation());
        operations.put(OperationType.WITHDRAWAL, new WithdrawalOperation());
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
    public void testUpdateBalanceReplenishmentSuccess() {
        AmountChangeRequest request = AmountChangeRequest.builder()
                .operationType(OperationType.REPLENISHMENT)
                .changeBalanceType(ChangeBalanceType.ACTUAL)
                .amount(BigDecimal.valueOf(100))
                .build();
        Operation operation = operations.get(OperationType.REPLENISHMENT);

        when(balanceRepository.findById(1L)).thenReturn(Optional.of(balance));
        when(operationRegistry.getOperation(OperationType.REPLENISHMENT)).thenReturn(operation);
        when(changeRegistry.getBalanceChange(ChangeBalanceType.ACTUAL)).thenReturn(new ActualBalanceChanger());
        balanceDto = balanceService.changeBalance(1L, request);

        verify(balanceRepository).save(any(Balance.class));
        assertEquals(balanceDto.actualBalance(), BigDecimal.valueOf(200));
    }

    @Test
    public void testUpdateBalanceWithdrawalSuccess() {
        AmountChangeRequest request = AmountChangeRequest.builder()
                .operationType(OperationType.WITHDRAWAL)
                .changeBalanceType(ChangeBalanceType.AUTHORIZATION)
                .amount(BigDecimal.valueOf(100))
                .build();
        Operation operation = operations.get(OperationType.WITHDRAWAL);

        when(balanceRepository.findById(1L)).thenReturn(Optional.of(balance));
        when(operationRegistry.getOperation(OperationType.WITHDRAWAL)).thenReturn(operation);
        when(changeRegistry.getBalanceChange(ChangeBalanceType.AUTHORIZATION))
                .thenReturn(new AuthorizationBalanceChanger());
        balanceDto = balanceService.changeBalance(1L, request);

        verify(balanceRepository).save(any(Balance.class));
        assertEquals(balanceDto.authBalance(), BigDecimal.ZERO);
    }

    @Test
    public void testUpdateBalanceFailWithdrawalNotEnoughBalance() {
        AmountChangeRequest request = AmountChangeRequest.builder()
                .operationType(OperationType.WITHDRAWAL)
                .changeBalanceType(ChangeBalanceType.ACTUAL)
                .amount(BigDecimal.valueOf(200))
                .build();
        Operation operation = operations.get(OperationType.WITHDRAWAL);

        when(balanceRepository.findById(1L)).thenReturn(Optional.of(balance));
        when(operationRegistry.getOperation(OperationType.WITHDRAWAL)).thenReturn(operation);
        when(changeRegistry.getBalanceChange(ChangeBalanceType.ACTUAL))
                .thenReturn(new ActualBalanceChanger());

        assertThrows(InsufficientFundsException.class, () -> balanceService.changeBalance(1L, request));
    }

    @Test
    public void testUpdateBalanceFailAuthMoreThanActual() {
        AmountChangeRequest request = AmountChangeRequest.builder()
                .operationType(OperationType.REPLENISHMENT)
                .changeBalanceType(ChangeBalanceType.AUTHORIZATION)
                .amount(BigDecimal.valueOf(200))
                .build();
        Operation operation = operations.get(OperationType.REPLENISHMENT);

        when(balanceRepository.findById(1L)).thenReturn(Optional.of(balance));
        when(operationRegistry.getOperation(OperationType.REPLENISHMENT)).thenReturn(operation);
        when(changeRegistry.getBalanceChange(ChangeBalanceType.AUTHORIZATION))
                .thenReturn(new AuthorizationBalanceChanger());

        assertThrows(InsufficientFundsException.class, () -> balanceService.changeBalance(1L, request));
    }
}
