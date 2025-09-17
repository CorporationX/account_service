package faang.school.accountservice.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AccountNumberValidator.class)
public @interface ValidAccountNumber {

    String message() default "Размер счета с префиксом не может быть больше 20 знаков и меньше 12";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}


