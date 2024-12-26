package faang.school.accountservice.validator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FreeAccountNumberValidatorTest {

    private final FreeAccountNumberValidator freeAccountNumberValidator = new FreeAccountNumberValidator();

    @Test
    void validateNumberSequenceIsNotExceededTest() {
        long numberSequence = 100L;
        int accountNumberLength = 16;
        int accountTypeIdentity = 4455;

        assertDoesNotThrow(() -> freeAccountNumberValidator.validateNumberSequenceIsNotExceeded(
                numberSequence, accountNumberLength, accountTypeIdentity));
    }

    @Test
    void validateNumberSequenceIsNotExceededThrowsExceptionTest() {
        long numberSequence = 1000000000000L;
        int accountNumberLength = 16;
        int accountTypeIdentity = 4455;

        assertThrows(InternalError.class,
                () -> freeAccountNumberValidator.validateNumberSequenceIsNotExceeded(
                numberSequence, accountNumberLength, accountTypeIdentity));
    }
}