package faang.school.accountservice.dto.request;

import faang.school.accountservice.enums.request.RequestStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateRequestDto(
        @NotNull
        RequestStatus status,
        String statusDetails
) {
}
