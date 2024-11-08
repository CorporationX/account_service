package faang.school.accountservice.service.impl;

import faang.school.accountservice.dto.RequestDto;
import faang.school.accountservice.entity.Request;
import faang.school.accountservice.exception.DataValidationException;
import faang.school.accountservice.mapper.RequestMapper;
import faang.school.accountservice.publisher.RequestStatusPublisher;
import faang.school.accountservice.repository.RequestRepository;
import faang.school.accountservice.service.RequestService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;
    private final RequestStatusPublisher requestStatusPublisher;

    @Override
    public RequestDto createRequest(@Valid RequestDto requestDto) {
        Request request = requestMapper.toEntity(requestDto);
        requestDto = requestMapper.toDto(requestRepository.save(request));
        requestStatusPublisher.publish(requestMapper.toEvent(requestDto));
        return requestDto;
    }

    @Override
    public RequestDto updateRequest(RequestDto updateDto) {
        Request request = getRequest(updateDto.getId());
        requestMapper.updateEntityFromDto(updateDto, request);
        RequestDto requestDto = requestMapper.toDto(requestRepository.save(request));

        if (updateDto.getStatus() != null) {
            requestStatusPublisher.publish(requestMapper.toEvent(requestDto));
        }
        return requestDto;
    }

    private Request getRequest(UUID id) {
        if (id == null) {
            throw new DataValidationException("Id is null");
        }
        return requestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Request with id %s not found".formatted(id)));
    }
}
