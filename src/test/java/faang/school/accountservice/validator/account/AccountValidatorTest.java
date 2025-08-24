package faang.school.accountservice.validator.account;

import faang.school.accountservice.dto.account.CreateAccountDto;
import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.exception.DataValidationException;
import faang.school.accountservice.exception.account.AccountOwnershipException;
import faang.school.accountservice.exception.account.IllegalStatusTransitionException;
import faang.school.accountservice.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountValidatorTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountValidator validator;

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
    void validateCreateThrowsWhenAccountNumberAlreadyExists() {
        CreateAccountDto dto = newAccountDto(1L, null);
        when(accountRepository.existsByAccountNumber(dto.accountNumber())).thenReturn(true);

        assertThrows(DataValidationException.class,
                () -> validator.validateCreate(dto));

        verify(accountRepository).existsByAccountNumber(dto.accountNumber());
    }

    @Test
    void validateCreatePassesWhenAccountNumberIsUnique() {
        CreateAccountDto dto = newAccountDto(1L, null);
        when(accountRepository.existsByAccountNumber(dto.accountNumber())).thenReturn(false);

        assertDoesNotThrow(() -> validator.validateCreate(dto));

        verify(accountRepository).existsByAccountNumber(dto.accountNumber());
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
