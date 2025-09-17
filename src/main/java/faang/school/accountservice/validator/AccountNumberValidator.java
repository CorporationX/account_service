package faang.school.accountservice.validator;

import faang.school.accountservice.controller.AccountNumberController;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class AccountNumberValidator implements ConstraintValidator<ValidAccountNumber, AccountNumberController.GenerateAccountNumberRequest> {
    @Override
    public boolean isValid(AccountNumberController.GenerateAccountNumberRequest s, ConstraintValidatorContext constraintValidatorContext) {
        return (s.getPrefix().length() + s.getAccountLength() <= 20) && (s.getPrefix().length() + s.getAccountLength() >= 12);
    }
}
