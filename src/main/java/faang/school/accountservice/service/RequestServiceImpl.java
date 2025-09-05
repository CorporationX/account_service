package faang.school.accountservice.service;

import faang.school.accountservice.enums.PaymentMessageType;
import faang.school.accountservice.enums.PaymentStages;
import faang.school.accountservice.model.Request;
import faang.school.accountservice.model.dto.PaymentMessageDto;
import faang.school.accountservice.repository.RequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;

    @Transactional
    public Request createRequest(PaymentMessageDto message, PaymentMessageType requestType, String lockValue) {
        Request request = Request.builder()
                .id(message.getIdempotencyToken())
                .userId(message.getFromAccountId())
                .requestType(requestType)
                .lockValue(lockValue)
                .isOpen(true)
                .status(PaymentStages.PENDING)
                .inputData(Map.of(
                        "toAccountId", message.getToAccountId(),
                        "amount", message.getAmount(),
                        "currency", message.getCurrency(),
                        "scheduledAt", message.getScheduledAt()
                ))
                .build();
        return requestRepository.save(request);
    }

    @Transactional
    public Request updateStatus(UUID requestId,
                                PaymentStages newStatus,
                                String statusDetails) {

        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));

        request.setStatus(newStatus);
        request.setStatusDetails(statusDetails);

        if (newStatus == PaymentStages.CLEARED ||
            newStatus == PaymentStages.CANCELED ||
            newStatus == PaymentStages.FAILED) {
            request.setIsOpen(false);
        }

        return requestRepository.save(request);
    }

    @Transactional
    public Request getRequest(UUID requestId) {
        return requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));
    }

    @Transactional
    public List<Request> findPendingRequestsToClear() {
        return requestRepository.findByIsOpenAndStatus(true, PaymentStages.PENDING);
    }
}