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
                .statusDetails("Created")
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
        request.setRequestStatus(statusDto.requestStatus());
        request.setStatusDetails(statusDto.statusDetails());
        request.setNotificationPending(true);
        request.setPendingNotificationType(NotificationType.STATUS_UPDATED);

        return mapper.toDto(requestRepository.save(request));
    }

    @Override
    @Transactional
    public RequestResponseDto updateFlagRequest(long id, RequestUpdateFlagDto dto) {
        Request request = getRequest(id);
        request.setOpen(dto.isOpen());
        request.setNotificationPending(true);
        request.setPendingNotificationType(NotificationType.FLAG_UPDATED);

        return mapper.toDto(requestRepository.save(request));
    }

    @Override
    @Transactional
    public RequestResponseDto updateContextRequest(long id, RequestUpdateContextDto dto) {
        Request request = getRequest(id);
        request.setInputRequest(dto.inputRequest());
        request.setNotificationPending(true);
        request.setPendingNotificationType(NotificationType.CONTEXT_UPDATED);

        return mapper.toDto(requestRepository.save(request));
    }

    private Request getRequest(long id) {
        return requestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Request not found: %d".formatted(id)));
    }
}
