package faang.school.accountservice.validator.account;

import faang.school.accountservice.dto.account.CreateAccountDto;
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
        createAccountDto.setAccountNumber("       ");

        assertThrows(IllegalArgumentException.class,
                ()->accountServiceValidator.validateCreateAccountDto(createAccountDto));
    }

    @Test
    void testValidateCreateAccountDtoThrowExceptionWhenEnumIsNull(){
        createAccountDto.setAccountNumber("12345678901234");

        assertThrows(IllegalArgumentException.class,
                ()->accountServiceValidator.validateCreateAccountDto(createAccountDto));
    }
}