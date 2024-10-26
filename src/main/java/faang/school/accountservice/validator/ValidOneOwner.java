package faang.school.accountservice.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = OneOwnerValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidOneOwner {
    String message() default "one account can't be owned by user and project at same time";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

