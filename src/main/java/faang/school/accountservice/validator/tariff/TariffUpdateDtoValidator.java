package faang.school.accountservice.validator.tariff;

import faang.school.accountservice.dto.tariff.TariffUpdateDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.math.BigDecimal;

public class TariffUpdateDtoValidator implements ConstraintValidator<ValidTariffUpdateDto, TariffUpdateDto> {

    private static final int RATE_PRECISION_MAX = 4;
    private static final int RATE_INTEGER_MAX = 2;
    private static final int RATE_SCALE_MAX = 2;
    private static final BigDecimal RATE_VALUE_MIN = BigDecimal.valueOf(0.01);
    private static final BigDecimal RATE_VALUE_MAX = BigDecimal.valueOf(99.99);
    private static final int TARIFF_NAME_LENGTH_MAX = 128;

    @Override
    public void initialize(ValidTariffUpdateDto constraintAnnotation) { }

    @Override
    public boolean isValid(TariffUpdateDto updateDto, ConstraintValidatorContext context) {
        return validateNotBothEmptyFields(updateDto, context) && validateTariffRateValue(updateDto.getRate(), context)
                && validateTariffNameLength(updateDto.getName(), context);
    }

    private boolean validateNotBothEmptyFields(TariffUpdateDto updateDto, ConstraintValidatorContext context) {
        if (updateDto.getRate() == null && (updateDto.getName() == null || updateDto.getName().isBlank())) {
            setupContext(context, "Either 'rate' or 'name' must be provided, both cannot be empty/null.");
            return false;
        }
        return true;
    }

    private boolean validateTariffRateValue(BigDecimal rate, ConstraintValidatorContext context) {
        if (rate != null) {
            int precision = rate.precision();
            int scale = rate.scale();
            int integer = precision - scale;
            if (precision > RATE_PRECISION_MAX || scale > RATE_SCALE_MAX || integer > RATE_INTEGER_MAX
                    || rate.compareTo(RATE_VALUE_MIN) < 0 || rate.compareTo(RATE_VALUE_MAX) > 0) {
                String responseMessage = String.format(
                        "Rate must be between %s and %s, with a maximum of %d digits in the integer part and a maximum of %d digits in the fractional part.",
                        RATE_VALUE_MIN, RATE_VALUE_MAX, RATE_INTEGER_MAX, RATE_SCALE_MAX
                );
                setupContext(context, responseMessage);
                return false;
            }
        }
        return true;
    }

    private boolean validateTariffNameLength(String tariffName, ConstraintValidatorContext context) {
        if (tariffName != null && tariffName.length() > TARIFF_NAME_LENGTH_MAX) {
            setupContext(context, "Max tariff name length = %d".formatted(TARIFF_NAME_LENGTH_MAX));
            return false;
        }
        return true;
    }

    private void setupContext(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
    }
}