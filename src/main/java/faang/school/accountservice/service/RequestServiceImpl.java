package faang.school.accountservice.service;

import faang.school.accountservice.enums.PaymentMessageType;
import faang.school.accountservice.enums.PaymentStages;
import faang.school.accountservice.mapper.RequestMapper;
import faang.school.accountservice.model.Request;
import faang.school.accountservice.model.dto.PaymentMessageDto;
import faang.school.accountservice.model.dto.RequestDto;
import faang.school.accountservice.repository.RequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Реализация сервиса работы с заявками.
 * <p>
 * Управляет созданием, обновлением и поиском заявок.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public RequestDto createRequestNewTransaction(PaymentMessageDto message, PaymentMessageType requestType, String lockValue) {
        Map<String, Object> inputData = buildInputData(message);

        Request request = Request.builder()
                .id(message.getIdempotencyToken())
                .userId(message.getFromAccountId())
                .requestType(requestType)
                .lockValue(lockValue)
                .isOpen(true)
                .status(PaymentStages.PENDING)
                .inputData(inputData)
                .build();

        Request saved = requestRepository.save(request);
        log.info("Создана заявка (new transaction) {} для пользователя {}, тип: {}",
                saved.getId(), saved.getUserId(), requestType);
        return requestMapper.toDto(saved);
    }

    @Override
    @Transactional
    public void handleAuthorizationMessage(PaymentMessageDto message) {
        UUID requestId = message.getIdempotencyToken();
        RequestDto request = requestRepository.findById(requestId)
                .map(requestMapper::toDto)
                .orElse(null);

        if (request == null) {
            log.warn("Заявка не найдена для AUTHORIZATION: {}", requestId);
            return;
        }

        PaymentStages status = PaymentStages.valueOf(request.getStatus());
        if (PaymentStages.FAILED.equals(status)
            || PaymentStages.CLEARED.equals(status)
            || PaymentStages.CANCELED.equals(status)) {
            log.warn("Заявка {} в финальном статусе {}, пропускаем AUTHORIZATION", requestId, status);
            return;
        }

        try {
            log.info("Обработка AUTHORIZATION для заявки {}", requestId);
            updateStatus(requestId, PaymentStages.AUTHORIZED, "Платеж авторизован");
        } catch (Exception ex) {
            log.error("Ошибка при обработке AUTHORIZATION для заявки {}: {}", requestId, ex.getMessage(), ex);
        }
    }

    @Override
    @Transactional
    public void handleCancelMessage(PaymentMessageDto message) {
        UUID requestId = message.getIdempotencyToken();
        RequestDto request = requestRepository.findById(requestId)
                .map(requestMapper::toDto)
                .orElse(null);

        if (request == null) {
            log.warn("Заявка не найдена для CANCEL: {}", requestId);
            return;
        }

        PaymentStages status = PaymentStages.valueOf(request.getStatus());
        if (PaymentStages.FAILED.equals(status)
            || PaymentStages.CLEARED.equals(status)
            || PaymentStages.CANCELED.equals(status)) {
            log.warn("Заявка {} в финальном статусе {}, пропускаем CANCEL", requestId, status);
            return;
        }

        try {
            log.info("Обработка CANCEL для заявки {}", requestId);
            updateStatus(requestId, PaymentStages.CANCELED, "Платеж отменен");
        } catch (Exception ex) {
            log.error("Ошибка при обработке CANCEL для заявки {}: {}", requestId, ex.getMessage(), ex);
        }
    }

    @Override
    @Transactional
    public void handleClearingMessage(PaymentMessageDto message) {
        UUID requestId = message.getIdempotencyToken();
        RequestDto request = requestRepository.findById(requestId)
                .map(requestMapper::toDto)
                .orElse(null);

        if (request == null) {
            log.warn("Заявка не найдена для CLEARING: {}", requestId);
            return;
        }

        PaymentStages status = PaymentStages.valueOf(request.getStatus());
        if (PaymentStages.FAILED.equals(status)
            || PaymentStages.CLEARED.equals(status)
            || PaymentStages.CANCELED.equals(status)) {
            log.warn("Заявка {} в финальном статусе {}, пропускаем CLEARING", requestId, status);
            return;
        }

        try {
            log.info("Обработка CLEARING для заявки {}", requestId);
            updateStatus(requestId, PaymentStages.CLEARED, "Клиринг проведён");
        } catch (Exception ex) {
            log.error("Ошибка при обработке CLEARING для заявки {}: {}", requestId, ex.getMessage(), ex);
        }
    }

    @Override
    @Transactional
    public RequestDto createRequest(PaymentMessageDto message, PaymentMessageType requestType, String lockValue) {
        Map<String, Object> inputData = buildInputData(message);

        Request request = Request.builder()
                .id(message.getIdempotencyToken())
                .userId(message.getFromAccountId())
                .requestType(requestType)
                .lockValue(lockValue)
                .isOpen(true)
                .status(PaymentStages.PENDING)
                .inputData(inputData)
                .build();

        Request saved = requestRepository.save(request);
        log.info("Создана заявка {} для пользователя {}, тип: {}", saved.getId(), saved.getUserId(), requestType);
        return requestMapper.toDto(saved);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public RequestDto updateStatus(UUID requestId, PaymentStages newStatus, String statusDetails) {
        Request request = getRequestOrThrow(requestId);
        request.setStatus(newStatus);
        request.setStatusDetails(statusDetails);

        if (shouldClose(newStatus)) {
            request.setIsOpen(false);
            log.info("Заявка {} закрыта, новый статус: {}", requestId, newStatus);
        } else {
            log.info("Статус заявки {} обновлён на {}", requestId, newStatus);
        }

        return requestMapper.toDto(requestRepository.save(request));
    }

    @Override
    @Transactional(readOnly = true)
    public RequestDto getRequest(UUID requestId) {
        return requestMapper.toDto(getRequestOrThrow(requestId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestDto> findPendingRequestsToClear() {
        List<Request> pending = requestRepository.findByIsOpenAndStatus(true, PaymentStages.PENDING);
        log.info("Найдено {} открытых заявок для клиринга", pending.size());
        return requestMapper.toDtoList(pending);
    }

    private Request getRequestOrThrow(UUID requestId) {
        return requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Заявка не найдена: " + requestId));
    }

    private boolean shouldClose(PaymentStages status) {
        return status == PaymentStages.CLEARED || status == PaymentStages.CANCELED || status == PaymentStages.FAILED;
    }

    private Map<String, Object> buildInputData(PaymentMessageDto message) {
        Map<String, Object> map = new HashMap<>();
        map.put("toAccountId", message.getToAccountId());
        map.put("amount", message.getAmount());
        map.put("currency", message.getCurrency());
        if (message.getScheduledAt() != null) {
            map.put("scheduledAt", message.getScheduledAt());
        }
        return map;
    }
}