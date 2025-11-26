package faang.school.accountservice.dto;

import faang.school.accountservice.model.RequestStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RequestUpdateStatusDto(
        @NotNull
        RequestStatus requestStatus,
        @NotBlank
        String statusDetails
) {
}
