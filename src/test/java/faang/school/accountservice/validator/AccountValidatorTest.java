package faang.school.accountservice.validator;

import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.exception.AccountNotFoundException;
import faang.school.accountservice.model.account.Account;
import faang.school.accountservice.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountValidatorTest {

    private final long USER_ID = 12;
    private final long ACCOUNT_ID = 17;

    @InjectMocks
    private AccountValidator accountValidator;
    @Mock
    private AccountRepository accountRepository;

    private Account account;

    @BeforeEach
    void setUp() {
        account = Account.builder()
                .number("40817810099910004312")
                .ownerId(USER_ID)
                .accountType(AccountType.FL)
                .currency(Currency.RUB)
                .status(AccountStatus.OPEN)
                .build();
    }

    @Test
    void testValidateAccountWithException() {
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.empty());
        assertThrows(AccountNotFoundException.class, () -> accountValidator.validateAccount(ACCOUNT_ID));
    }

    @Test
    void testValidateAccountSuccess() {
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(account));
        assertDoesNotThrow(() -> accountValidator.validateAccount(ACCOUNT_ID));
    }

    @Test
    void testCheckStatusCloseAccountWithException() {
        account.setStatus(AccountStatus.CLOSE);
        assertThrows(IllegalArgumentException.class, () -> accountValidator.checkStatusCloseAccount(account));
    }

    @Test
    void testCheckStatusCloseAccountSuccess() {
        account.setStatus(AccountStatus.OPEN);
        assertDoesNotThrow(() -> accountValidator.checkStatusCloseAccount(account));
    }

    @Test
    void testCheckStatusOpenAccountWithException() {
        account.setStatus(AccountStatus.CLOSE);
        assertThrows(IllegalArgumentException.class, () -> accountValidator.checkStatusOpenAccount(account));
    }

    @Test
    void testCheckStatusOpenAccountSuccess() {
        account.setStatus(AccountStatus.OPEN);
        assertDoesNotThrow(() -> accountValidator.checkStatusOpenAccount(account));
    }

    @Test
    void testCheckStatusFreezeAccountWithException() {
        account.setStatus(AccountStatus.CLOSE);
        assertThrows(IllegalArgumentException.class, () -> accountValidator.checkStatusFreezeAccount(account));
    }

    @Test
    void testCheckStatusFreezeAccountSuccess() {
        account.setStatus(AccountStatus.FREEZE);
        assertDoesNotThrow(() -> accountValidator.checkStatusFreezeAccount(account));
    }

    @Test
    void testCheckAccountToUserWithException() {
        account.setOwnerId(2L);
        assertThrows(IllegalArgumentException.class, () -> accountValidator.checkAccountToUser(account, USER_ID));
    }

    @Test
    void testCheckAccountToUserSuccess() {
        assertDoesNotThrow(() -> accountValidator.checkAccountToUser(account, USER_ID));
    }

}
