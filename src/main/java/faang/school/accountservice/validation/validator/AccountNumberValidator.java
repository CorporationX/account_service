package faang.school.accountservice.validation.validator;

import faang.school.accountservice.validation.ValidAccountNumber;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AccountNumberValidator implements ConstraintValidator<ValidAccountNumber, String> {

    private static final String ACCOUNT_NUMBER_REGEX = "^ACC\\d{10}$";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && value.matches(ACCOUNT_NUMBER_REGEX);
    }
}