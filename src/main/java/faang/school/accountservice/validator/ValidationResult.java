package faang.school.accountservice.validator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class ValidationResult {
    private final boolean valid;
    private final String errorMessage;

    public static ValidationResult success() {
        return new ValidationResult(true, null);
    }

    public static ValidationResult failure(String errorMessage) {
        return new ValidationResult(false, errorMessage != null ? errorMessage : "Unknown validation error");
    }

    public static ValidationResult getCommonResult(List<ValidationResult> results) {
        if (results.stream().allMatch(ValidationResult::isValid)) {
            return ValidationResult.success();
        }

        String resultMessage = results.stream()
                .filter(result -> !result.isValid())
                .map(ValidationResult::getErrorMessage)
                .collect(Collectors.joining("; "));

        return ValidationResult.failure(resultMessage);
    }
}
