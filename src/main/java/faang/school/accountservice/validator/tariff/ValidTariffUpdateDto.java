package faang.school.accountservice.validator.tariff;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TariffUpdateDtoValidator.class)
public @interface ValidTariffUpdateDto {

    String message() default "Either 'rate' or 'name' must be provided, both cannot be null.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
