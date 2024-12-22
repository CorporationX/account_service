package faang.school.accountservice.validator.account;

import faang.school.accountservice.dto.account.CreateAccountDto;
import faang.school.accountservice.entity.account.Currency;
import faang.school.accountservice.entity.account.Type;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccountServiceValidatorTest {
    private AccountServiceValidator accountServiceValidator;

    private CreateAccountDto createAccountDto;
    @BeforeEach
    void setUp(){
        accountServiceValidator = new AccountServiceValidator();

        createAccountDto = new CreateAccountDto();
    }
    @Test
    void testCheckIdThrowException(){
        assertThrows(IllegalArgumentException.class,
                ()->accountServiceValidator.checkId(-23));
    }

    @Test
    void testValidateCreateAccountDtoThrowExceptionWhenCreateAccountDtoIsNull(){
        assertThrows(IllegalArgumentException.class,
                ()->accountServiceValidator.validateCreateAccountDto(null));
    }

    @Test
    void testValidateCreateAccountDtoThrowExceptionWhenInvalidAccount_number(){
        createAccountDto.setType(Type.FOREX_ACCOUNT);

        assertThrows(IllegalArgumentException.class,
                ()->accountServiceValidator.validateCreateAccountDto(createAccountDto));
    }

    @Test
    void testValidateCreateAccountDtoThrowExceptionWhenEnumIsNull(){
        createAccountDto.setCurrency(Currency.EUR);

        assertThrows(IllegalArgumentException.class,
                ()->accountServiceValidator.validateCreateAccountDto(createAccountDto));
    }
}