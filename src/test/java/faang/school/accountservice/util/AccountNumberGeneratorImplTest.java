package faang.school.accountservice.util;

import static org.junit.jupiter.api.Assertions.*;

import faang.school.accountservice.enums.AccountType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AccountNumberGeneratorImplTest {

    private AccountNumberGeneratorImpl accountNumberGenerator;
    private AccountType type;

    @BeforeEach
    void setUp() {
        accountNumberGenerator = new AccountNumberGeneratorImpl();
        type = AccountType.CHECKING_INDIVIDUAL;
    }

    @Test
    void testGenerateAccountNumber_CheckingIndividualType() {
        String expectedAccountNumber = "42000000000000123456";
        Long number = 123456L;

        String accountNumber = accountNumberGenerator.generateAccountNumber(type, number);

        assertEquals(20, accountNumber.length());
        assertEquals(expectedAccountNumber, accountNumber);
    }

    @Test
    void testGenerateAccountNumber_ExactLength() {
        Long number = 12345678910111213L;
        String correctMessage = "Number length exceeds the maximum allowed length of 16 digits";

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> accountNumberGenerator.generateAccountNumber(type, number));

        assertEquals(correctMessage, exception.getMessage());
    }
}
