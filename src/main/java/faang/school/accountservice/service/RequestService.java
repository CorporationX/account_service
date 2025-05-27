package faang.school.accountservice.service;

import faang.school.accountservice.dto.Request.RequestDto;
import faang.school.accountservice.dto.Request.RequestStatusDto;

import java.util.Map;

public interface RequestService {

    String createRequest(RequestDto requestDto, String idempotencyToken);

    RequestDto updateRequestStatus(Long requestId, RequestStatusDto requestStatusDto);

    RequestDto updateIsOpenFlag(Long requestId, boolean isOpen);

    RequestDto updateInputData(Long requestId, Map<String, Object> inputData);

}
