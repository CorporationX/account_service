package faang.school.accountservice.validation;

import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OwnerType;
import faang.school.accountservice.exception.AccountNotFoundException;
import faang.school.accountservice.exception.BalanceNotFoundException;
import faang.school.accountservice.exception.InsufficientBalanceException;
import faang.school.accountservice.exception.InvalidBalanceOperationException;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.BalanceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BalanceValidatorTest {
    private final static long DEFAULT_ID = 1L;
    private final static String DEFAULT_ACCOUNT_NUMBER = "12345678901234567890";
    private final static BigDecimal ONE_HUNDRED_AMOUNT = BigDecimal.valueOf(100);
    private final static BigDecimal TWO_HUNDRED_AMOUNT = BigDecimal.valueOf(200);
    private final static BigDecimal FIFTY_AMOUNT = BigDecimal.valueOf(50);
    private final static BigDecimal NEGATIVE_AMOUNT = BigDecimal.valueOf(-1);

    private final long balanceId = DEFAULT_ID;
    private final long accountId = DEFAULT_ID;
    private final BigDecimal oneHundredBalance = ONE_HUNDRED_AMOUNT;
    private final BigDecimal fiftyAmount = FIFTY_AMOUNT;
    private final BigDecimal twoHundredAmount = TWO_HUNDRED_AMOUNT;
    private final BigDecimal negativeAmount = NEGATIVE_AMOUNT;
    private final String accountNumber = DEFAULT_ACCOUNT_NUMBER;
    private final long userId = DEFAULT_ID;
    private final OwnerType ownerType = OwnerType.USER;
    private final AccountType accountType = AccountType.CURRENCY;
    private final Currency currency = Currency.USD;
    private final AccountStatus accountStatus = AccountStatus.ACTIVE;

    Account account = Account.builder()
            .accountNumber(accountNumber)
            .ownerId(userId)
            .ownerType(ownerType)
            .accountType(accountType)
            .currency(currency)
            .status(accountStatus)
            .balance(null)
            .build();

    Balance balance = Balance.builder()
            .account(account)
            .actualBalance(oneHundredBalance)
            .authorizationBalance(fiftyAmount)
            .build();


    @InjectMocks
    BalanceValidator balanceValidator;

    @Mock
    AccountRepository accountRepository;

    @Mock
    BalanceRepository balanceRepository;

    @Test
    public void testSuccessfullyBalanceExistValidated() {
        when(balanceRepository.findById(balanceId)).thenReturn(Optional.ofNullable(balance));
        assertDoesNotThrow(() -> balanceValidator.validateBalanceExisting(balanceId));
    }

    @Test
    public void testSuccessfullyAccountExistValidated() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.ofNullable(account));
        assertDoesNotThrow(() -> balanceValidator.validateAccountExisting(accountId));
    }

    @Test
    public void testSuccessfullyAmountValidated() {
        assertDoesNotThrow(() -> balanceValidator.validateAmount(oneHundredBalance));
    }

    @Test
    public void testSuccessfullyActualBalanceValidated() {
        assertDoesNotThrow(() -> balanceValidator.validateEnoughActualBalance(balance, fiftyAmount));
    }

    @Test
    public void testSuccessfullyAuthorizationBalanceValidated() {
        assertDoesNotThrow(() -> balanceValidator.validateEnoughAuthorizationBalance(balance, fiftyAmount));
    }

    @Test
    public void testFailBalanceExistValidatedWhenBalanceDoesNotExist() {
        when(balanceRepository.findById(balanceId)).thenReturn(Optional.empty());
        assertThrows(BalanceNotFoundException.class,
                () -> balanceValidator.validateBalanceExisting(balanceId));
    }

    @Test
    public void testFailAccountExistValidatedWhenBalanceDoesNotExist() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());
        assertThrows(AccountNotFoundException.class,
                () -> balanceValidator.validateAccountExisting(accountId));
    }

    @Test
    public void testFailAmountValidated() {
        assertThrows(InvalidBalanceOperationException.class,
                () -> balanceValidator.validateAmount(negativeAmount));
    }

    @Test
    public void testFailActualBalanceValidated() {
        assertThrows(InsufficientBalanceException.class,
                () -> balanceValidator.validateEnoughActualBalance(balance, twoHundredAmount));
    }

    @Test
    public void testFailAuthorizationBalanceValidated() {
        assertThrows(InsufficientBalanceException.class,
                () -> balanceValidator.validateEnoughActualBalance(balance, twoHundredAmount));
    }
}
