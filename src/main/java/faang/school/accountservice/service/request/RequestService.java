package faang.school.accountservice.service.request;

import faang.school.accountservice.dto.CreateRequestDto;
import faang.school.accountservice.enums.RequestStatus;
import faang.school.accountservice.model.Request;

import java.util.Map;
import java.util.UUID;

public interface RequestService {
    Request createRequest(CreateRequestDto request);
    void updateStatus(UUID token, RequestStatus status, String description);
    void closeRequest(UUID token);
    void updateInputContext(UUID token, Map<String, Object> input);
}
