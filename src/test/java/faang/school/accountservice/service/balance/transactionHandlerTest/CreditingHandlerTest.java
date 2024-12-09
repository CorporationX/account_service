package faang.school.accountservice.service.balance.transactionHandlerTest;

import faang.school.accountservice.dto.balance.TransactionDto;
import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.entity.balance.Balance;
import faang.school.accountservice.entity.owner.Owner;
import faang.school.accountservice.entity.type.AccountType;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.TransactionType;
import faang.school.accountservice.service.balance.transactionHandler.CreditingHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

@ExtendWith(MockitoExtension.class)
class CreditingHandlerTest {

    private TransactionDto transactionDto;
    private Account account;
    private Balance balance;

    @InjectMocks
    private CreditingHandler creditingHandler;

    @Mock
    private Owner owner;

    @Mock
    private AccountType accountType;

    @BeforeEach
    void init() {
        transactionDto = TransactionDto.builder()
                .paymentNumber(12345L)
                .transactionType(TransactionType.CREDITING)
                .amount(BigDecimal.valueOf(100))
                .currency(Currency.RUB)
                .build();

        balance = Balance.builder()
                .authorizationBalance(BigDecimal.valueOf(100.00))
                .actualBalance(BigDecimal.valueOf(150.00))
                .paymentNumber(12345L)
                .createdAt(LocalDateTime.of(2023, 10, 1, 12, 0))
                .updatedAt(LocalDateTime.of(2023, 10, 5, 12, 0))
                .build();

        account = Account.builder()
                .number("123456789012")
                .owner(owner)
                .accountType(accountType)
                .currency(Currency.USD)
                .status(AccountStatus.ACTIVE)
                .balance(balance)
                .build();
    }

    @Test
    @DisplayName("successful execution of method getType")
    void getTypeTest() {
        TransactionType type = creditingHandler.getType();
        assertSame(TransactionType.CREDITING, type);
    }

    @Test
    @DisplayName("successful execution of method handle")
    void handleTest() {
        BigDecimal newBalance = balance.getActualBalance().add(transactionDto.getAmount());

        Balance result = creditingHandler.handle(transactionDto, account);

        assertEquals(0, result.getActualBalance().compareTo(newBalance));
    }
}
