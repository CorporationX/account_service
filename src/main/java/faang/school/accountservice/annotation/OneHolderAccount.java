package faang.school.accountservice.annotation;

import faang.school.accountservice.validator.OneHolderAccountValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = OneHolderAccountValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface OneHolderAccount {

    String message() default "One of the fields must be filled: holderUserId or holderProjectId.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
