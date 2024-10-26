package faang.school.accountservice.validator;

import faang.school.accountservice.dto.PaymentAccountDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class OneOwnerValidator implements ConstraintValidator<ValidOneOwner, PaymentAccountDto> {
    @Override
    public boolean isValid(PaymentAccountDto paymentAccountDto, ConstraintValidatorContext context) {
        return (paymentAccountDto.getProjectId() != null && paymentAccountDto.getUserId() == null) ||
                (paymentAccountDto.getProjectId() == null && paymentAccountDto.getUserId() != null);
    }
}
