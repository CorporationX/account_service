package faang.school.accountservice.service;

import faang.school.accountservice.dto.RequestCreateDto;
import faang.school.accountservice.dto.RequestResponseDto;
import faang.school.accountservice.dto.RequestUpdateContextDto;
import faang.school.accountservice.dto.RequestUpdateFlagDto;
import faang.school.accountservice.dto.RequestUpdateStatusDto;
import faang.school.accountservice.mapper.RequestMapper;
import faang.school.accountservice.model.NotificationType;
import faang.school.accountservice.model.Request;
import faang.school.accountservice.model.RequestStatus;
import faang.school.accountservice.repository.RequestRepository;
import faang.school.accountservice.service.notification.NotificationMessageFactory;
import faang.school.accountservice.service.notification.publisher.MessagePublisher;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final MessagePublisher messagePublisher;
    private final NotificationMessageFactory messageFactory;

    @Override
    @Transactional
    public RequestResponseDto createRequest(RequestCreateDto requestCreateDto) {
        UUID idempotencyKey = UUID.randomUUID();
        Request request = new Request();
        request.setIdempotencyKey(idempotencyKey);
        request.setUserId(requestCreateDto.userId());
        request.setRequestType(requestCreateDto.requestType());
        request.setLockKey(requestCreateDto.lockKey());
        request.setInputRequest(requestCreateDto.inputRequest());
        request.setOpen(true);
        request.setRequestStatus(RequestStatus.TO_DO);
        request.setStatusDetails("Created");
        request.setCreatedAt(OffsetDateTime.now());
        request.setUpdatedAt(OffsetDateTime.now());
        request.setNotificationPending(true);
        request.setPendingNotificationType(NotificationType.CREATED);

        requestRepository.save(request);

        return RequestMapper.toDto(request);
    }

    @Override
    @Transactional
    public RequestResponseDto updateStatusRequest(long id, RequestUpdateStatusDto statusDto) {
        Request request = getRequest(id);
        request.setRequestStatus(statusDto.requestStatus());
        request.setStatusDetails(statusDto.statusDetails());
        request.setUpdatedAt(OffsetDateTime.now());
        request.setNotificationPending(true);
        request.setPendingNotificationType(NotificationType.STATUS_UPDATED);

        return RequestMapper.toDto(requestRepository.save(request));
    }

    @Override
    @Transactional
    public RequestResponseDto updateFlagRequest(long id, RequestUpdateFlagDto dto) {
        Request request = getRequest(id);
        request.setOpen(dto.isOpen());
        request.setUpdatedAt(OffsetDateTime.now());
        request.setNotificationPending(true);
        request.setPendingNotificationType(NotificationType.FLAG_UPDATED);

        return RequestMapper.toDto(requestRepository.save(request));
    }

    @Override
    @Transactional
    public RequestResponseDto updateContextRequest(long id, RequestUpdateContextDto dto) {
        Request request = getRequest(id);
        request.setInputRequest(dto.inputRequest());
        request.setUpdatedAt(OffsetDateTime.now());
        request.setNotificationPending(true);
        request.setPendingNotificationType(NotificationType.CONTEXT_UPDATED);

        return RequestMapper.toDto(requestRepository.save(request));
    }

    private Request getRequest(long id) {
        return requestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Request not found: " + id));
    }
}
