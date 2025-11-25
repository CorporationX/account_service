package faang.school.accountservice.dto;

import faang.school.accountservice.model.RequestStatus;

public record RequestUpdateStatusDto(
        RequestStatus requestStatus,
        String statusDetails
) {
}
