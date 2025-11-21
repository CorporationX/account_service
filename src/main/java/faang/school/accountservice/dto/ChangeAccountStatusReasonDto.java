package faang.school.accountservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangeAccountStatusReasonDto(
        @NotBlank
        @Size(max = 255)
        String reason
) {
}

