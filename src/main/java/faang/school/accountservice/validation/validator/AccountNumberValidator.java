package faang.school.accountservice.validation.validator;

import faang.school.accountservice.validation.ValidAccountNumber;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AccountNumberValidator implements ConstraintValidator<ValidAccountNumber, String> {

    private static final String ACCOUNT_NUMBER_REGEX = "\\d+";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        boolean onlyDigits = value.matches(ACCOUNT_NUMBER_REGEX);
        boolean startsWithNoZero = value.charAt(0) != '0';
        boolean validLength = value.length() > 11 && value.length() < 21;

        return onlyDigits && startsWithNoZero && validLength;
    }
}