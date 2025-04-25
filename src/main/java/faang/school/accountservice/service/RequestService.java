package faang.school.accountservice.service;

import faang.school.accountservice.dto.CreateRequestDto;
import faang.school.accountservice.enums.RequestStatus;

import java.util.Map;
import java.util.UUID;

public interface RequestService {
    void createRequest(CreateRequestDto createRequestDto);

    void updateRequestStatusByToken(UUID requestToken, RequestStatus newRequestStatus);

    void updateRequestBodyByToken(UUID requestToken, Map<String, Object> newBody);
}
