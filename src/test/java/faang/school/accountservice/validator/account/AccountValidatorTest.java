package faang.school.accountservice.validator.account;

import faang.school.accountservice.dto.account.CreateAccountDto;
import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.exception.account.AccountOwnershipException;
import faang.school.accountservice.exception.account.IllegalStatusTransitionException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccountValidatorTest {

    private final AccountValidator validator = new AccountValidator();

    private CreateAccountDto newAccountDto(Long userId, Long projectId) {
        return new CreateAccountDto("123456789012",
                userId,
                projectId,
                AccountType.PERSONAL,
                Currency.EUR,
                AccountStatus.ACTIVE
        );
    }

    private Account accountWithStatus(AccountStatus status) {
        return Account.builder().status(status).build();
    }

    @Test
    void validateCreateThrowsIfBothUserAndProjectNull() {
        assertThrows(AccountOwnershipException.class, () -> validator.validateCreate(newAccountDto(null, null)));
    }

    @Test
    void validateCreateThrowsIfBothUserAndProjectPresent() {
        assertThrows(AccountOwnershipException.class, () -> validator.validateCreate(newAccountDto(1L, 2L)));
    }

    @Test
    void validateCreateOkWhenOnlyUserPresent() {
        assertDoesNotThrow(() -> validator.validateCreate(newAccountDto(1L, null)));
    }

    @Test
    void validateCreateOkWhenOnlyProjectPresent() {
        assertDoesNotThrow(() -> validator.validateCreate(newAccountDto(null, 2L)));
    }

    @Test
    void validateStatusTransitionThrowsWhenSameStatus() {
        Account account = accountWithStatus(AccountStatus.ACTIVE);
        assertThrows(IllegalStatusTransitionException.class,
                () -> validator.validateStatusTransition(account, AccountStatus.ACTIVE));
    }

    @Test
    void validateStatusTransitionThrowsWhenCurrentClosed() {
        Account account = accountWithStatus(AccountStatus.CLOSED);
        assertThrows(IllegalStatusTransitionException.class,
                () -> validator.validateStatusTransition(account, AccountStatus.FROZEN));
    }

    @Test
    void validateStatusTransitionAllowsActiveToFrozen() {
        Account account = accountWithStatus(AccountStatus.ACTIVE);
        assertDoesNotThrow(() -> validator.validateStatusTransition(account, AccountStatus.FROZEN));
    }

    @Test
    void validateStatusTransitionAllowsFrozenToClosed() {
        Account account = accountWithStatus(AccountStatus.FROZEN);
        assertDoesNotThrow(() -> validator.validateStatusTransition(account, AccountStatus.CLOSED));
    }
}
