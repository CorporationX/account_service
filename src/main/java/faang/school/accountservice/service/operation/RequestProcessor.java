package faang.school.accountservice.service.operation;

import faang.school.accountservice.dto.request.RequestEventDto;
import faang.school.accountservice.entity.request.Request;
import faang.school.accountservice.enums.request.RequestStatus;
import faang.school.accountservice.exception.EntityNotFoundException;
import faang.school.accountservice.publisher.RequestStatusPublisher;
import faang.school.accountservice.repository.RequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestProcessor {

    private final RequestRepository requestRepository;
    private final OperationHandlerRegistry operationHandlerRegistry;
    private final RequestStatusPublisher requestStatusPublisher;

    @Transactional
    public void process(UUID idempotencyToken) {
        Request request = requestRepository.findById(idempotencyToken)
                .orElseThrow(() -> new EntityNotFoundException("Request not found: " + idempotencyToken));

        try {
            request.changeStatus(RequestStatus.PROCESSING, "Operation started");
            requestRepository.save(request);
            publishEvent(request);

            OperationHandler handler = operationHandlerRegistry.getHandler(request.getOperationType());
            log.info("Starting operation with handler: {}", handler.getClass().getSimpleName());
            handler.execute(request);

            request.changeStatus(RequestStatus.COMPLETED, "Success");
            log.info("Operation completed by handler: {}", handler.getClass().getSimpleName());
        } catch (Exception e) {
            String errorMessage = String.format("%s: %s", e.getClass().getSimpleName(), e.getMessage());
            request.changeStatus(RequestStatus.FAILED, e.getMessage());
            log.error("Operation failed: {}", e.getMessage(), e);
        }

        request = requestRepository.save(request);
        publishEvent(request);
    }

    private void publishEvent(Request request) {
        try {
            requestStatusPublisher.publish(new RequestEventDto(
                    request.getIdempotencyToken(),
                    request.getUserId(),
                    request.getOperationType(),
                    request.getRequestStatus(),
                    LocalDateTime.now()
            ));
        } catch (Exception e) {
            log.error("Failed to publish event for request {}", request.getIdempotencyToken(), e);
        }
    }
}
