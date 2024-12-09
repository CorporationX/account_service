package faang.school.accountservice.service.balance.transactionHandlerTest;

import faang.school.accountservice.dto.balance.TransactionDto;
import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.entity.balance.Balance;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.TransactionType;
import faang.school.accountservice.exception.BalanceException;
import faang.school.accountservice.service.balance.transactionHandler.RetentionHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class RetentionHandlerTest {

    private TransactionDto transactionDto;
    private Account account;
    private Balance balance;

    @InjectMocks
    private RetentionHandler retentionHandler;

    @Test
    @DisplayName("successful execution of method getType")
    void getTypeTest() {
        TransactionType type = retentionHandler.getType();
        assertSame(TransactionType.RETENTION, type);
    }

    @Test
    @DisplayName("successful execution of method handle")
    void handleTest() {
        createTransactionDto(12345L, BigDecimal.valueOf(100));
        setupAccountWithBalance(BigDecimal.valueOf(100.00), BigDecimal.valueOf(150.00), 12345L);

        BigDecimal newActualBalance = balance.getActualBalance().subtract(transactionDto.getAmount());

        Balance result = retentionHandler.handle(transactionDto, account);

        assertEquals(result.getAuthorizationBalance(), transactionDto.getAmount());
        assertEquals(result.getActualBalance(), newActualBalance);
        assertEquals(result.getPaymentNumber(), transactionDto.getPaymentNumber());
    }

    @Test
    @DisplayName("throws BalanceException when Amount > ActualBalance")
    void handleTestWhenAmountMoreActualBalance() {
        createTransactionDto(12345L, BigDecimal.valueOf(400));
        setupAccountWithBalance(BigDecimal.valueOf(100.00), BigDecimal.valueOf(150.00), 12345L);

        assertThrows(BalanceException.class, () -> retentionHandler.handle(transactionDto, account));
    }

    private void setupAccountWithBalance(BigDecimal authorizationBalance, BigDecimal actualBalance, long paymentNumber) {
        balance = Balance.builder()
                .authorizationBalance(authorizationBalance)
                .actualBalance(actualBalance)
                .paymentNumber(paymentNumber)
                .build();

        account = Account.builder()
                .currency(Currency.RUB)
                .balance(balance)
                .build();
    }

    private void createTransactionDto(long paymentNumber, BigDecimal amount) {
        transactionDto = TransactionDto.builder()
                .paymentNumber(paymentNumber)
                .transactionType(TransactionType.DEBITING)
                .amount(amount)
                .currency(Currency.RUB)
                .build();
    }
}
