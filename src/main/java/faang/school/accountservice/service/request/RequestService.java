package faang.school.accountservice.service.request;

import faang.school.accountservice.dto.request.RequestCreationDto;
import faang.school.accountservice.dto.request.RequestResponseDto;
import faang.school.accountservice.dto.request.RequestUpdateDto;

public interface RequestService {
    RequestResponseDto createRequest(RequestCreationDto requestCreationDto);

    RequestResponseDto updateRequest(RequestUpdateDto requestDto);
}
