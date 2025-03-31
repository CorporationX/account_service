package faang.school.accountservice.service.request;

import faang.school.accountservice.dto.RequestDto;
import faang.school.accountservice.entity.Request;
import faang.school.accountservice.exception.DataValidationException;
import faang.school.accountservice.exception.DuplicateKeyException;
import faang.school.accountservice.mapper.RequestMapper;
import faang.school.accountservice.publisher.RequestStatusPublisher;
import faang.school.accountservice.repository.RequestRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;
    private final RequestStatusPublisher requestStatusPublisher;

    @Override
    @Transactional
    public RequestDto createRequest(@Valid RequestDto requestDto) {
        Request request = requestMapper.toEntity(requestDto);

        try {
            request = requestRepository.save(request);
        } catch (DataIntegrityViolationException ex) {
            throw new DuplicateKeyException("Request with created_by="
                    + requestDto.createdBy() + " already exists.", ex);
        }

        requestDto = requestMapper.toDto(request);
        requestStatusPublisher.publish(requestMapper.toEvent(requestDto));
        return requestDto;
    }

    @Override
    @Transactional
    public RequestDto updateRequest(RequestDto updateDto) {
        Request request = getRequest(updateDto.id());
        requestMapper.updateEntityFromDto(updateDto, request);

        RequestDto requestDto = requestMapper.toDto(requestRepository.save(request));

        if (request.getRequestStatus() != null) {
            requestStatusPublisher.publish(requestMapper.toEvent(requestDto));
        }
        return requestDto;
    }

    private Request getRequest(UUID id) {
        if (id == null) {
            throw new DataValidationException("the UUID id cannot be null!");
        }
        return requestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Request with id %s not found".formatted(id)));
    }
}
