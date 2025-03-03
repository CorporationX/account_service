package faang.school.accountservice.annotation;

import faang.school.accountservice.validator.AccountNumberValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AccountNumberValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface AccountNumberConstraint {

    String message() default "Invalid account number";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
