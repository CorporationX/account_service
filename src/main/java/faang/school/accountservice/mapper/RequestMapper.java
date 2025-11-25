package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.RequestResponseDto;
import faang.school.accountservice.model.Request;

public class RequestMapper {
    public static RequestResponseDto toDto(Request request) {
        return new RequestResponseDto(
                request.getIdempotencyKey(),
                request.getUserId(),
                request.getRequestType(),
                request.getRequestStatus(),
                request.isOpen(),
                request.getInputRequest(),
                request.getStatusDetails()
        );
    }
}
