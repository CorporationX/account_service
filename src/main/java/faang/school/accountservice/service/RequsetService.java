package faang.school.accountservice.service;

import faang.school.accountservice.dto.RequestDto;
import faang.school.accountservice.entity.IdempotencyToken;
import faang.school.accountservice.enums.RequestStatus;

import java.util.Map;

public interface RequsetService {

    String createRequest(RequestDto requestDto, IdempotencyToken idempotencyToken);

    RequestDto updateRequestStatus(Long requestId, RequestStatus status, String statusDetails);

    RequestDto updateIsOpenFlag(Long requestId, boolean isOpen);

    RequestDto updateInputData(Long requestId, Map<String, Object> inputData);

    RequestDto getRequestById(Long requestId);
}
