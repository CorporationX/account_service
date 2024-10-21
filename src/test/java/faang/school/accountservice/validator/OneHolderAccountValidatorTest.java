package faang.school.accountservice.validator;

import faang.school.accountservice.dto.AccountDto;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OneHolderAccountValidatorTest {

    private OneHolderAccountValidator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new OneHolderAccountValidator();
        context = null;
    }

    @Test
    void isValid_ShouldReturnFalse_WhenAccountDtoIsNull() {
        boolean result = validator.isValid(null, context);

        assertFalse(result);
    }

    @Test
    void isValid_ShouldReturnTrue_WhenOnlyHolderUserIdIsPresent() {
        AccountDto accountDto = new AccountDto();
        accountDto.setHolderUserId(1L);
        accountDto.setHolderProjectId(null);

        boolean result = validator.isValid(accountDto, context);

        assertTrue(result);
    }

    @Test
    void isValid_ShouldReturnTrue_WhenOnlyHolderProjectIdIsPresent() {
        AccountDto accountDto = new AccountDto();
        accountDto.setHolderUserId(null);
        accountDto.setHolderProjectId(1L);

        boolean result = validator.isValid(accountDto, context);

        assertTrue(result);
    }

    @Test
    void isValid_ShouldReturnFalse_WhenBothHolderUserIdAndHolderProjectIdArePresent() {
        AccountDto accountDto = new AccountDto();
        accountDto.setHolderUserId(1L);
        accountDto.setHolderProjectId(2L);

        boolean result = validator.isValid(accountDto, context);

        assertFalse(result);
    }

    @Test
    void isValid_ShouldReturnFalse_WhenBothHolderUserIdAndHolderProjectIdAreNull() {
        AccountDto accountDto = new AccountDto();
        accountDto.setHolderUserId(null);
        accountDto.setHolderProjectId(null);

        boolean result = validator.isValid(accountDto, context);

        assertFalse(result);
    }
}
