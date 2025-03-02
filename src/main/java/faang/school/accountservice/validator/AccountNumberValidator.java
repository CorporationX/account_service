package faang.school.accountservice.validator;

import faang.school.accountservice.annotation.AccountNumberConstraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AccountNumberValidator implements ConstraintValidator<AccountNumberConstraint, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return false;
        } else {
            return value.length() >= 12 && value.length() <= 20;
        }
    }
}
