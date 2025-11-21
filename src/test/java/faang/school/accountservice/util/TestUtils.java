package faang.school.accountservice.util;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.function.Executable;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestUtils {

    public static <T extends RuntimeException> void assertThrowsWithMessage(
            Class<T> expectedType,
            String expectedMessage,
            Executable executable) {

        T exception = assertThrows(expectedType, executable);
        assertEquals(expectedMessage, exception.getMessage());
    }

    public static void assertTimestamp(LocalDateTime actual) {
        LocalDateTime now = LocalDateTime.now();
        long diffMillis = Math.abs(Duration.between(actual, now).toMillis());

        assertTrue(diffMillis <= 100,
                () -> "Dates are not almost equal. Difference = %d ms. Expected: %s, Actual: %s"
                        .formatted(diffMillis, now, actual));
    }

    public static void validateAccountNumber(String accountNumber) {
        String accountNumberRegexp = "^\\d{12,20}$";
        Pattern pattern = Pattern.compile(accountNumberRegexp);

        Assertions.assertTrue(pattern.matcher(accountNumber).matches());
    }
}
