package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.RequestResponseDto;
import faang.school.accountservice.model.Request;
import faang.school.accountservice.model.RequestStatus;
import faang.school.accountservice.model.RequestType;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RequestMapperTest {
    @Test
    void toDto_shouldMapAllFieldsCorrectly() {
        UUID idempotencyKey = UUID.randomUUID();
        long userId = 123L;
        RequestType requestType = RequestType.IN_PROGRESS;
        RequestStatus requestStatus = RequestStatus.TO_DO;
        boolean isOpen = true;
        Map<String, Object> inputRequest = new HashMap<>();
        inputRequest.put("amount", 100);
        String statusDetails = "Created";

        Request request = new Request();
        request.setIdempotencyKey(idempotencyKey);
        request.setUserId(userId);
        request.setRequestType(requestType);
        request.setRequestStatus(requestStatus);
        request.setOpen(isOpen);
        request.setInputRequest(inputRequest);
        request.setStatusDetails(statusDetails);

        RequestResponseDto dto = RequestMapper.toDto(request);

        assertEquals(idempotencyKey, dto.idempotencyKey());
        assertEquals(userId, dto.userId());
        assertEquals(requestType, dto.requestType());
        assertEquals(requestStatus, dto.requestStatus());
        assertEquals(isOpen, dto.isOpen());
        assertEquals(inputRequest, dto.inputRequest());
        assertEquals(statusDetails, dto.statusDetails());
    }
}
