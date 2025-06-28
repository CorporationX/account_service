package faang.school.accountservice.validation.account;

import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.entity.account.AccountStatus;
import faang.school.accountservice.exception.account.AccountAlreadyCloseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class AccountValidationTest {
    @InjectMocks
    private AccountValidator accountValidator;
    private Account account;

    @BeforeEach
    public void setUp() {
        account = new Account();
        account.setId(UUID.randomUUID());
    }

    @Test
    public void testCheckCloseAccount_successfully() {
        account.setStatus(AccountStatus.BLOKE);

        assertDoesNotThrow(() -> accountValidator.checkCloseAccount(account));
    }

    @Test
    public void testCheckCloseAccount_accountAlreadyClose() {
        account.setStatus(AccountStatus.CLOSE);

        assertThrows(AccountAlreadyCloseException.class, () -> accountValidator.checkCloseAccount(account));
    }

}
