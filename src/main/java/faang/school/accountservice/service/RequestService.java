package faang.school.accountservice.service;

import faang.school.accountservice.config.context.UserContext;
import faang.school.accountservice.dto.request.CreateRequestDto;
import faang.school.accountservice.dto.request.ResponseRequestDto;
import faang.school.accountservice.enums.RequestStatus;
import faang.school.accountservice.enums.RequestType;
import faang.school.accountservice.mapper.RequestMapper;
import faang.school.accountservice.model.PaymentAccount;
import faang.school.accountservice.model.Request;
import faang.school.accountservice.repository.RequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestService {

    private static final String PAYMENT_ACCOUNT_NOT_FOUND = "Payment account with id %d not found";

    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;
    private final UserContext userContext;
    private final PaymentAccountService paymentAccountService;

    public ResponseRequestDto createRequest(CreateRequestDto createRequestDto) {
        validatePaymentAccountExistence(createRequestDto.paymentAccountId());

        Request transientRequest = requestMapper.toRequest(createRequestDto);
        Request persistedRequest = requestRepository.findByIdempotencyToken(transientRequest.getIdempotencyToken())
                .orElse(null);

        if (persistedRequest != null) {
            return requestMapper.toResponseRequestDto(persistedRequest);
        }

        try {
            transientRequest.setUserId(userContext.getUserId());
            transientRequest.setOpen(true);
            transientRequest.setRequestStatus(RequestStatus.TODO);
            requestRepository.save(transientRequest);

        } catch (DataIntegrityViolationException ex) {
            Request existingRequest = requestRepository.findByIdempotencyToken(transientRequest.getIdempotencyToken())
                    .orElse(null);
            if (existingRequest != null) {
                return requestMapper.toResponseRequestDto(existingRequest);
            }
        }
        return requestMapper.toResponseRequestDto(transientRequest);
    }

    private void validatePaymentAccountExistence(Long paymentAccountId) {
        if (!paymentAccountService.existsByAccountId(paymentAccountId)) {
            throw new DataIntegrityViolationException(String.format(PAYMENT_ACCOUNT_NOT_FOUND, paymentAccountId));
        }
    }

    public List<Request> getRequestsToExecute(RequestType requestType, int limit) {
        return requestRepository.getRequestToExecuteByType(requestType.name(), limit);
    }

    public void updateRequestStatus(Long requestId, RequestStatus requestStatus) {
        requestRepository.updateRequestStatusById(requestId, requestStatus.name());
    }

    public void completeRequest(Long requestId) {
        requestRepository.completeRequest(requestId);
    }
}
