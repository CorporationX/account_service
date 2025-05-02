package faang.school.accountservice.service;

import faang.school.accountservice.dto.Request.RequestDto;
import faang.school.accountservice.dto.Request.RequestStatusDto;
import faang.school.accountservice.entity.IdempotencyToken;

import java.util.Map;

public interface RequestService {

    String createRequest(RequestDto requestDto, IdempotencyToken idempotencyToken);

    RequestDto updateRequestStatus(Long requestId, RequestStatusDto requestStatusDto);

    RequestDto updateIsOpenFlag(Long requestId, boolean isOpen);

    RequestDto updateInputData(Long requestId, Map<String, Object> inputData);

}
