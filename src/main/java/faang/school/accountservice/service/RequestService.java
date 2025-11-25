package faang.school.accountservice.service;

import faang.school.accountservice.dto.RequestCreateDto;
import faang.school.accountservice.dto.RequestResponseDto;
import faang.school.accountservice.dto.RequestUpdateContextDto;
import faang.school.accountservice.dto.RequestUpdateFlagDto;
import faang.school.accountservice.dto.RequestUpdateStatusDto;

public interface RequestService {
    RequestResponseDto createRequest(RequestCreateDto requestCreateDto);

    RequestResponseDto updateStatusRequest(long id, RequestUpdateStatusDto statusDto);

    RequestResponseDto updateFlagRequest(long id, RequestUpdateFlagDto flagDto);

    RequestResponseDto updateContextRequest(long id, RequestUpdateContextDto contextDto);
}
