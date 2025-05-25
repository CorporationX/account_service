package faang.school.accountservice.service.request;

import faang.school.accountservice.dto.request.RequestCreationDto;
import faang.school.accountservice.dto.request.RequestResponseDto;
import faang.school.accountservice.dto.request.RequestStatusResponseDto;
import faang.school.accountservice.dto.request.RequestUpdateDto;

import java.util.UUID;

public interface RequestService {
    RequestResponseDto createRequest(RequestCreationDto requestCreationDto);

    RequestResponseDto updateRequest(RequestUpdateDto requestDto);

    RequestStatusResponseDto getStatus(UUID id);
}
