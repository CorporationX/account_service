package faang.school.accountservice.service;

import faang.school.accountservice.dto.request.RequestCreateDto;
import faang.school.accountservice.dto.request.RequestResponseDto;
import faang.school.accountservice.entity.Request;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

public interface RequestService {

    RequestResponseDto createRequest(RequestCreateDto requestCreateDto);

    void updateRequest(Request request);

    RequestResponseDto getRequest(UUID idempotencyKey) throws AccessDeniedException;

    void deleteRequest(UUID idempotencyKey) throws AccessDeniedException;
}
