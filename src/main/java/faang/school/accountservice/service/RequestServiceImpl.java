package faang.school.accountservice.service;

import faang.school.accountservice.converter.JsonConverter;
import faang.school.accountservice.dto.RequestDto;
import faang.school.accountservice.entity.Request;
import faang.school.accountservice.exception.InvalidUserException;
import faang.school.accountservice.exception.RequestNotFoundException;
import faang.school.accountservice.mapper.RequestMapper;
import faang.school.accountservice.repository.RequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;
    private final JsonConverter converter;

    @Override
    @Transactional
    public RequestDto createRequest(RequestDto requestDto) {
        if (isNotAvailableToCreateRequest(requestDto)) {
            throw new InvalidUserException("User with id " + requestDto.getUserId() + " has open operations");
        }
        Request request = requestMapper.toEntity(requestDto);
        Map<String, Object> inputData = converter.convertToMap(requestDto.getRequestInput());
        request.setInputData(inputData);
        request.setIdempotencyToken(UUID.randomUUID());
        requestRepository.save(request);
        return requestMapper.toDto(request);
    }

    @Override
    @Transactional
    public RequestDto updateStatus(RequestDto requestDto) {
        Request request = getRequestIfPresent(requestDto);
        request.setRequestStatus(requestDto.getRequestStatus());
        return requestMapper.toDto(request);
    }

    @Override
    @Transactional
    public RequestDto updateIsOpen(RequestDto requestDto) {
        Request request = getRequestIfPresent(requestDto);
        request.setOpen(requestDto.getIsOpen());
        return requestMapper.toDto(request);
    }

    @Override
    @Transactional
    public RequestDto updateInputData(RequestDto requestDto) {
        Request request = getRequestIfPresent(requestDto);
        Map<String, Object> inputData = converter.convertToMap(requestDto.getRequestInput());
        request.setInputData(inputData);
        Request savedRequest = requestRepository.save(request);
        return requestMapper.toDto(savedRequest);
    }

    private Request getRequestIfPresent(RequestDto requestDto) {
        Optional<Request> request = requestRepository.findByIdempotencyToken(requestDto.getIdempotencyToken());
        if (request.isPresent()) {
            return request.get();
        } else {
            throw new RequestNotFoundException("Request with id " + requestDto.getIdempotencyToken() + " doesn't exist");
        }
    }

    private boolean isNotAvailableToCreateRequest(RequestDto requestDto) {
        return !requestRepository.findByLockValueAndIsOpen(requestDto.getUserId()).isEmpty();
    }
}
