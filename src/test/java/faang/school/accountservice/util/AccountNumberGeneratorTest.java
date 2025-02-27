package faang.school.accountservice.util;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.security.SecureRandom;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AccountNumberGeneratorTest {

    private final SecureRandom secureRandom = new SecureRandom();
    private final AccountNumberGenerator generator = new AccountNumberGenerator(secureRandom);

    @Test
    void generate_ShouldReturnStringOfDigitsBetween12And20() {
        for (int i = 0; i < 100; i++) {
            String accountNumber = generator.generate();

            assertNotNull(accountNumber);
            assertTrue(accountNumber.matches("\\d{12,20}"), "Account number should contain only digits");
            assertTrue(accountNumber.length() >= 12 && accountNumber.length() <= 20,
                    "Account number length should be between 12 and 20");
        }
    }

    @RepeatedTest(100)
    void generate_ShouldReturnValidNumberForRandomValues() {
        String accountNumber = generator.generate();

        assertNotNull(accountNumber);
        assertTrue(accountNumber.matches("\\d{12,20}"), "Account number should contain only digits");
        assertTrue(accountNumber.length() >= 12 && accountNumber.length() <= 20,
                "Account number length should be between 12 and 20");
    }
}