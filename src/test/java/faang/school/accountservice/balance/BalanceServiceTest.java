package faang.school.accountservice.balance;

import faang.school.accountservice.config.context.UserContext;
import faang.school.accountservice.dto.BalanceResponseDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.entity.Transaction;
import faang.school.accountservice.enums.TransactionType;
import faang.school.accountservice.exception.*;
import faang.school.accountservice.repository.BalanceRepository;
import faang.school.accountservice.repository.TransactionsRepository;
import faang.school.accountservice.service.BalanceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BalanceServiceTest {

    @InjectMocks
    private BalanceService service;
    @Mock
    UserContext context;
    @Mock
    private BalanceRepository balanceRepository;
    @Mock
    private TransactionsRepository repository;


    private static final Long TEST_BALANCE_ID = 1L;
    private static final Long TEST_USER_ID = 123L;
    private static final Long TEST_OTHER_USER_ID = 456L;
    private static final BigDecimal TEST_AMOUNT = BigDecimal.valueOf(500);
    private static final String TEST_COMMENT = "Комментарий";

    @Test
    public void positiveGetBalance() {
        Balance balance = createTestBalance(TEST_BALANCE_ID, TEST_USER_ID, BigDecimal.TEN, BigDecimal.TEN);
        setupContextAndRepository(TEST_USER_ID, balance);

        BalanceResponseDto result = assertDoesNotThrow(() -> service.getBalance(TEST_BALANCE_ID));

        verify(balanceRepository).findById(TEST_BALANCE_ID);
        assertNotNull(result);
        assertEquals(BigDecimal.TEN, result.currentBalance());
        assertEquals(BigDecimal.TEN, result.availableBalance());
    }

    @Test
    public void negativeBalanceNotFound() {
        when(balanceRepository.findById(TEST_BALANCE_ID)).thenReturn(Optional.empty());

        BalanceNotFoundException exception = assertThrows(
                BalanceNotFoundException.class,
                () -> service.getBalance(TEST_BALANCE_ID)
        );

        verify(balanceRepository).findById(TEST_BALANCE_ID);
        assertTrue(exception.getMessage().contains("Баланс не найден"));
    }

    @Test
    public void negativeIsNotOwner() {
        Balance balance = createTestBalance(TEST_BALANCE_ID, TEST_OTHER_USER_ID, BigDecimal.TEN, BigDecimal.TEN);
        setupContextAndRepository(TEST_USER_ID, balance);

        AccessException exception = assertThrows(AccessException.class,
                () -> service.getBalance(TEST_BALANCE_ID));

        assertTrue(exception.getMessage().contains("Нет доступа к этому аккаунту"));
        verify(context, times(2)).getUserId();
    }

    @Test
    public void negativeNotFoundContext() {
        Balance balance = createTestBalance(TEST_BALANCE_ID, TEST_USER_ID, BigDecimal.TEN, BigDecimal.TEN);
        when(balanceRepository.findById(TEST_BALANCE_ID)).thenReturn(Optional.of(balance));
        when(context.getUserId()).thenReturn(0L);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.getBalance(TEST_BALANCE_ID));

        assertTrue(exception.getMessage().contains("Account или UserContext не инициализированы"));
    }

    @Test
    public void negativeNotFoundAccount() {
        Balance balance = new Balance();
        when(balanceRepository.findById(TEST_BALANCE_ID)).thenReturn(Optional.of(balance));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.getBalance(TEST_BALANCE_ID));

        assertTrue(exception.getMessage().contains("Account или UserContext не инициализированы"));
    }

    @Test
    void positiveAddFunds() {
        Balance balance = createTestBalance(TEST_BALANCE_ID, TEST_USER_ID,
                BigDecimal.valueOf(10000), BigDecimal.valueOf(8000));
        setupContextAndRepository(TEST_USER_ID, balance);

        when(balanceRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        assertDoesNotThrow(() -> service.addFunds(TEST_BALANCE_ID, TEST_AMOUNT, TEST_COMMENT));

        verify(balanceRepository).save(any());
        verify(repository).save(any());
    }

    @Test
    void negativeAddFundsWrongAmount() {
        Balance balance = createTestBalance(TEST_BALANCE_ID, TEST_USER_ID,
                BigDecimal.valueOf(5000), BigDecimal.valueOf(4000));
        setupContextAndRepository(TEST_USER_ID, balance);

        BigDecimal negativeAmount = BigDecimal.valueOf(-100);
        WrongAmountException exception = assertThrows(
                WrongAmountException.class,
                () -> service.addFunds(TEST_BALANCE_ID, negativeAmount, TEST_COMMENT)
        );

        verify(balanceRepository, never()).save(any());
        assertEquals("Депозит должен быть больше 0", exception.getMessage());
    }

    @Test
    void positiveWithdrawFunds() {
        Balance balance = createTestBalance(TEST_BALANCE_ID, TEST_USER_ID,
                BigDecimal.valueOf(10000), BigDecimal.valueOf(8000));
        setupContextAndRepository(TEST_USER_ID, balance);

        when(balanceRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        assertDoesNotThrow(() -> service.withdrawFunds(TEST_BALANCE_ID, TEST_AMOUNT, TEST_COMMENT));

        verify(balanceRepository).save(any());
        verify(repository).save(any());
    }

    @Test
    void negativeWithdrawFundsWrongAmount() {
        Balance balance = createTestBalance(TEST_BALANCE_ID, TEST_USER_ID,
                BigDecimal.valueOf(5000), BigDecimal.valueOf(4000));
        setupContextAndRepository(TEST_USER_ID, balance);

        BigDecimal negativeAmount = BigDecimal.valueOf(-100);
        WrongAmountException exception = assertThrows(
                WrongAmountException.class,
                () -> service.withdrawFunds(TEST_BALANCE_ID, negativeAmount, TEST_COMMENT)
        );

        verify(balanceRepository, never()).save(any());
        assertTrue(exception.getMessage().contains("Сумма должна быть больше 0"));
    }

    @Test
    void negativeWithdrawFundsNotEnoughMoney() {
        Balance balance = createTestBalance(TEST_BALANCE_ID, TEST_USER_ID,
                BigDecimal.valueOf(5000), BigDecimal.valueOf(4000));
        setupContextAndRepository(TEST_USER_ID, balance);

        BigDecimal tooLargeAmount = BigDecimal.valueOf(10000);
        NotEnoughFundsException exception = assertThrows(
                NotEnoughFundsException.class,
                () -> service.withdrawFunds(TEST_BALANCE_ID, tooLargeAmount, TEST_COMMENT)
        );

        verify(balanceRepository, never()).save(any());
        assertEquals("Недостаточно средств для списания", exception.getMessage());
    }

    @Test
    void positiveSendPayment() {
        Long receiverId = 2L;
        Balance sender = createTestBalance(TEST_BALANCE_ID, TEST_USER_ID,
                BigDecimal.valueOf(1000), BigDecimal.valueOf(1000));
        Balance receiver = createTestBalance(receiverId, TEST_OTHER_USER_ID,
                BigDecimal.valueOf(2000), BigDecimal.valueOf(2000));

        when(balanceRepository.findById(TEST_BALANCE_ID)).thenReturn(Optional.of(sender));
        when(balanceRepository.findById(receiverId)).thenReturn(Optional.of(receiver));
        when(context.getUserId()).thenReturn(TEST_USER_ID);

        when(repository.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));


        assertDoesNotThrow(() -> service.sendPayment(TEST_BALANCE_ID, receiverId, TEST_AMOUNT, TEST_COMMENT));

        ArgumentCaptor<List<Transaction>> transactionsCaptor = ArgumentCaptor.forClass(List.class);
        verify(repository).saveAll(transactionsCaptor.capture());
        verify(balanceRepository, never()).saveAll(anyList());

        List<Transaction> savedTransactions = transactionsCaptor.getValue();
        assertThat(savedTransactions.get(0).getType()).isEqualTo(TransactionType.SEND);
        assertThat(savedTransactions.get(1).getType()).isEqualTo(TransactionType.RECEIVE);

    }

    @Test
    void negativeSendPaymentNotEnoughFunds() {
        Long receiverId = 2L;
        Balance sender = createTestBalance(TEST_BALANCE_ID, TEST_USER_ID,
                BigDecimal.valueOf(1000), BigDecimal.valueOf(1000));
        Balance receiver = createTestBalance(receiverId, TEST_OTHER_USER_ID,
                BigDecimal.ZERO, BigDecimal.ZERO);

        when(balanceRepository.findById(TEST_BALANCE_ID)).thenReturn(Optional.of(sender));
        when(balanceRepository.findById(receiverId)).thenReturn(Optional.of(receiver));
        when(context.getUserId()).thenReturn(TEST_USER_ID);

        NotEnoughFundsException exception = assertThrows(
                NotEnoughFundsException.class,
                () -> service.sendPayment(TEST_BALANCE_ID, receiverId, BigDecimal.valueOf(1500), TEST_COMMENT)
        );

        verify(balanceRepository, never()).saveAll(anyList());
        assertEquals("Недостаточно средств для перевода", exception.getMessage());
    }

    @Test
    void negativeSendPaymentSelfSend() {
        Balance balance = createTestBalance(TEST_BALANCE_ID, TEST_USER_ID,
                BigDecimal.valueOf(1000), BigDecimal.valueOf(1000));
        when(balanceRepository.findById(TEST_BALANCE_ID)).thenReturn(Optional.of(balance));
        when(context.getUserId()).thenReturn(TEST_USER_ID);

        SelfPayException exception = assertThrows(
                SelfPayException.class,
                () -> service.sendPayment(TEST_BALANCE_ID, TEST_BALANCE_ID, BigDecimal.TEN, "self")
        );

        verify(balanceRepository, never()).saveAll(anyList());
        assertEquals("Нельзя переводить себе", exception.getMessage());
    }

    private Balance createTestBalance(Long balanceId,
                                      Long ownerId,
                                      BigDecimal currentBalance,
                                      BigDecimal availableBalance) {
        Account account = new Account();
        account.setOwnerId(ownerId);
        return new Balance(
                balanceId,
                account,
                currentBalance,
                availableBalance,
                LocalDateTime.now(),
                LocalDateTime.now(),
                new ArrayList<>(),
                0
        );
    }

    private void setupContextAndRepository(Long userId, Balance balance) {
        when(context.getUserId()).thenReturn(userId);
        when(balanceRepository.findById(TEST_BALANCE_ID)).thenReturn(Optional.ofNullable(balance));
    }
}