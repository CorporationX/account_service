package faang.school.accountservice.validator;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.enums.Currency;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class AccountValidatorTest {

    @InjectMocks
    private AccountValidator accountValidator;
    private AccountDto accountDto;

    @BeforeEach
    void setUp() {
        accountDto = new AccountDto();
    }

    @Test
    void testValidateAuthorExistThrowException() {
        assertThrows(IllegalArgumentException.class,
                () -> accountValidator.validateAuthorExist(accountDto));
    }

    @Test
    void testValidateAuthorExist() {
        accountDto.setOwnerId(1L);
        assertDoesNotThrow(() -> accountValidator.validateAuthorExist(accountDto));
    }

    @Test
    void testValidateCurrencySelectionThrowException() {
        assertThrows(IllegalArgumentException.class,
                () -> accountValidator.validateAuthorExist(accountDto));
    }

    @Test
    void testValidateCurrencySelection() {
        accountDto.setCurrency(Currency.RUB);
        assertDoesNotThrow(() -> accountValidator.validateCurrencySelection(accountDto));
    }
}
