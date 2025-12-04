package faang.school.accountservice.service;

import faang.school.accountservice.dto.RequestCreateDto;
import faang.school.accountservice.dto.RequestResponseDto;
import faang.school.accountservice.dto.RequestUpdateContextDto;
import faang.school.accountservice.dto.RequestUpdateFlagDto;
import faang.school.accountservice.dto.RequestUpdateStatusDto;
import faang.school.accountservice.exception.RequestNotFoundException;
import faang.school.accountservice.mapper.RequestMapper;
import faang.school.accountservice.model.NotificationType;
import faang.school.accountservice.model.Request;
import faang.school.accountservice.model.RequestStatus;
import faang.school.accountservice.repository.RequestRepository;
import faang.school.accountservice.service.notification.NotificationMessageFactory;
import faang.school.accountservice.service.notification.publisher.MessagePublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final MessagePublisher messagePublisher;
    private final NotificationMessageFactory messageFactory;
    private final RequestMapper mapper;

    @Override
    @Transactional
    public RequestResponseDto createRequest(RequestCreateDto requestCreateDto) {
        Request request = Request.builder()
                .userId(requestCreateDto.userId())
                .requestType(requestCreateDto.requestType())
                .lockKey(requestCreateDto.lockKey())
                .inputRequest(requestCreateDto.inputRequest())
                .open(true)
                .requestStatus(RequestStatus.TO_DO)
                .notificationPending(true)
                .pendingNotificationType(NotificationType.CREATED)
                .build();

        requestRepository.save(request);

        return mapper.toDto(request);
    }

    @Override
    @Transactional
    public RequestResponseDto updateStatusRequest(long id, RequestUpdateStatusDto statusDto) {
        Request request = getRequest(id);

        Request updated = request.toBuilder()
                .requestStatus(statusDto.requestStatus())
                .statusDetails(statusDto.statusDetails())
                .notificationPending(true)
                .pendingNotificationType(NotificationType.STATUS_UPDATED)
                .build();

        return mapper.toDto(requestRepository.save(updated));
    }

    @Override
    @Transactional
    public RequestResponseDto updateFlagRequest(long id, RequestUpdateFlagDto dto) {
        Request request = getRequest(id);

        Request updated = request.toBuilder()
                .open(dto.isOpen())
                .notificationPending(true)
                .pendingNotificationType(NotificationType.FLAG_UPDATED)
                .build();

        return mapper.toDto(requestRepository.save(updated));
    }

    @Override
    @Transactional
    public RequestResponseDto updateContextRequest(long id, RequestUpdateContextDto dto) {
        Request request = getRequest(id);

        Request updated = request.toBuilder()
                .inputRequest(dto.inputRequest())
                .notificationPending(true)
                .pendingNotificationType(NotificationType.CONTEXT_UPDATED)
                .build();

        return mapper.toDto(requestRepository.save(updated));
    }

    private Request getRequest(long id) {
        return requestRepository.findById(id)
                .orElseThrow(() -> new RequestNotFoundException(id));
    }
}
