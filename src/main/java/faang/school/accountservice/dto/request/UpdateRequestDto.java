package faang.school.accountservice.dto.request;

import faang.school.accountservice.enums.request.RequestStatus;

public record UpdateRequestDto(
        RequestStatus status,
        String statusDetails
) {
}
