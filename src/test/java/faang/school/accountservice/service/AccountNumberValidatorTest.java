package faang.school.accountservice.service;

import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.exception.InvalidAccountNumberException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatNoException;

class AccountNumberValidatorTest {

    private AccountNumberValidator validator;

    @BeforeEach
    void setUp() {
        validator = new AccountNumberValidator();
    }

    @Test
    @DisplayName("Should validate correct account number")
    void shouldValidateCorrectAccountNumber() {
        AccountType accountType = AccountType.DEBIT;
        String validNumber = "420000000001";

        assertThatNoException().isThrownBy(() ->
                validator.validate(accountType, validNumber));
    }

    @Test
    @DisplayName("Should throw exception for invalid account number")
    void shouldThrowExceptionForInvalidAccountNumber() {
        AccountType accountType = AccountType.CREDIT;
        String invalidNumber = "123456";

        assertThatThrownBy(() -> validator.validate(accountType, invalidNumber))
                .isInstanceOf(InvalidAccountNumberException.class)
                .hasMessageContaining("Invalid account number");
    }

    @Test
    @DisplayName("Should check validity without throwing exception")
    void shouldCheckValidityWithoutThrowingException() {
        AccountType accountType = AccountType.SAVINGS;

        assertThat(validator.isValid(accountType, "430000000001")).isTrue();
        assertThat(validator.isValid(accountType, "invalid")).isFalse();
        assertThat(validator.isValid(accountType, null)).isFalse();
    }
}