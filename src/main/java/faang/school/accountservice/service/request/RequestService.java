package faang.school.accountservice.service.request;

import faang.school.accountservice.dto.request.RequestDto;
import faang.school.accountservice.dto.request.RequestResponseDto;
import faang.school.accountservice.dto.request.RequestUpdateDto;

public interface RequestService {
    RequestResponseDto createRequest(RequestDto requestDto);

    RequestResponseDto updateRequest(RequestUpdateDto requestDto);
}
