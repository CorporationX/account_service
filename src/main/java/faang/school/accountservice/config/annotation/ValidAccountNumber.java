package faang.school.accountservice.config.annotation;


import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
@Size(min = 12, max = 20, message = "Account number must be between 12 and 20 characters")
@Pattern(regexp = "\\d+", message = "Account number must be numeric positive value")
@Parameter(description = "Account number. Value must be numeric", example = "12345678901234567890")
public @interface ValidAccountNumber {

    String message() default "Invalid account number";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
