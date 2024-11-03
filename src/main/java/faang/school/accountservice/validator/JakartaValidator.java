package faang.school.accountservice.validator;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JakartaValidator {

    private final Validator validator;

    public <T> ValidationResult validate(T object) {
        Set<ConstraintViolation<T>> violations = validator.validate(object);

        if (violations.isEmpty()) {
            return ValidationResult.success();
        }

        String message = violations.stream()
                .map(violation -> violation.getPropertyPath() + " " + violation.getMessage())
                .collect(Collectors.joining("; "));

        return ValidationResult.failure(message);
    }
}
