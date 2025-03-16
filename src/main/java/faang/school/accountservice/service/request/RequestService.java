package faang.school.accountservice.service.request;

import faang.school.accountservice.dto.RequestDto;

public interface RequestService {

    RequestDto createRequest(RequestDto requestDto);

    RequestDto updateRequest(RequestDto requestDto);
}
