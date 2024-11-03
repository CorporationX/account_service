package faang.school.accountservice.service.impl;

import faang.school.accountservice.exception.DataValidationException;
import faang.school.accountservice.model.entity.Account;
import faang.school.accountservice.model.entity.Balance;
import faang.school.accountservice.model.entity.Request;
import faang.school.accountservice.model.enums.RequestStatus;
import faang.school.accountservice.model.event.PaymentStatusEvent;
import faang.school.accountservice.model.event.RequestEvent;
import faang.school.accountservice.publisher.PaymentStatusEventPublisher;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.BalanceRepository;
import faang.school.accountservice.repository.RequestRepository;
import faang.school.accountservice.service.PaymentRequestService;
import faang.school.accountservice.validator.JakartaValidator;
import faang.school.accountservice.validator.PaymentEventValidator;
import faang.school.accountservice.validator.ValidationResult;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentRequestServiceImpl implements PaymentRequestService {
    private static final int SENT_TIME_PERIOD_IN_MINUTES = 15;

    private final AccountRepository accountRepository;
    private final BalanceRepository balanceRepository;
    private final RequestRepository requestRepository;
    private final JakartaValidator jakartaValidator;
    private final PaymentEventValidator validator;
    private final PaymentStatusEventPublisher paymentStatusEventPublisher;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    @Transactional
    public void authorize(RequestEvent requestEvent) {
        boolean activeRequestExists = requestRepository.existsByAccountIdAndStatus(requestEvent.getAccountId(), RequestStatus.IN_PROGRESS);

        if (activeRequestExists) {
            log.debug(String.format("An active payment request already exists for accountId = %d; ignoring duplicate request", requestEvent.getAccountId()));
            return;
        }

        Optional<Request> existingRequestOpt = requestRepository.findByIdempotencyToken(requestEvent.getIdempotencyToken());

        if (existingRequestOpt.isPresent()) {
            Request existingRequest = existingRequestOpt.get();

            switch (existingRequest.getStatus()) {
                //FIXME этого же не может быть? мы не дошли до сюда, вышли из метода тут if (activeRequestExists)
                case IN_PROGRESS:
                    log.debug(String.format("The request with id = %d, idempotencyToken = %s is already being processed; ignoring it",
                            existingRequest.getId(), existingRequest.getIdempotencyToken()));
                    return;

                case FAILED:
                    log.debug(String.format("The request with id = %d, idempotencyToken = %s previously failed; retrying it",
                            existingRequest.getId(), existingRequest.getIdempotencyToken()));
                    Request request = retryFailedRequest(existingRequest, requestEvent);
                    //TODO  добавить createAuditRecord
                    if (request.getStatus() == RequestStatus.IN_PROGRESS) {
                        applicationEventPublisher.publishEvent(new PaymentStatusEvent(request.getId(), request.getIdempotencyToken(), request.getStatus()));
                    }
                    return;

                case COMPLETED:
                case CANCELLED:
                    log.debug(String.format("The request with id = %d, idempotencyToken = %s is already completed/canceled; not executing it again",
                            existingRequest.getId(), existingRequest.getIdempotencyToken()));
                    return;

                default:
                    throw new DataValidationException(String.format("Unexpected request status: %s", existingRequest.getStatus()));
            }
        }

        ValidationResult commonResult = validatePaymentEvent(requestEvent);

        Map<String, Object> inputData = new HashMap<>();
        inputData.put("amount", requestEvent.getAmount());
        inputData.put("firstSentDateTime", requestEvent.getSentDateTime());
        inputData.put("lastSentDateTime", requestEvent.getSentDateTime());

        Request request = createRequest(requestEvent, inputData, commonResult);
        Request savedRequest = requestRepository.save(request);
        //TODO  добавить createAuditRecord
        applicationEventPublisher.publishEvent(new PaymentStatusEvent(savedRequest.getId(), savedRequest.getIdempotencyToken(), savedRequest.getStatus()));
    }

    @Override
    public void cancel(RequestEvent requestEvent) {

    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onRequestCompleted(PaymentStatusEvent event) {
        paymentStatusEventPublisher.publish(event);
    }

    private Request retryFailedRequest(Request existingRequest, RequestEvent requestEvent) {
        Map<String, Object> inputData = existingRequest.getInputData();

        if (inputData.containsKey("amount") && !inputData.get("amount").equals(requestEvent.getAmount())) {
            log.debug("Updating amount in request for retry. Old amount: " + inputData.get("amount") + ", new amount: " + requestEvent.getAmount());
            inputData.put("amount", requestEvent.getAmount());
            inputData.put("lastSentDateTime", requestEvent.getSentDateTime());
            existingRequest.setInputData(inputData);
        }

        ValidationResult retryResult = validatePaymentEvent(requestEvent);
        if (retryResult.isValid()) {
            existingRequest.setStatus(RequestStatus.IN_PROGRESS);
        } else {
            existingRequest.setStatus(RequestStatus.FAILED);
            existingRequest.setStatusDetails(retryResult.getErrorMessage());
        }

        return requestRepository.save(existingRequest);
    }

    private Request createRequest(RequestEvent requestEvent, Map<String, Object> inputData, ValidationResult commonResult) {
        Account account = accountRepository.findById(requestEvent.getAccountId()).orElseThrow(() ->
                new EntityNotFoundException(String.format("Account with id = %d not found", requestEvent.getAccountId())));
        Balance balance = account.getBalance();

        Request request = new Request();
        request.setIdempotencyToken(requestEvent.getIdempotencyToken());
        request.setAccount(account);
        request.setRequestType(requestEvent.getRequestType());
        request.setOperationType(requestEvent.getOperationType());
        request.setInputData(inputData);

        if (commonResult.isValid()) {
            balance.setAuthorizedBalance(requestEvent.getAmount());
            balance.setActualBalance(balance.getActualBalance().subtract(requestEvent.getAmount()));
            balanceRepository.save(balance);

            request.setStatus(RequestStatus.IN_PROGRESS);
        } else {
            request.setStatus(RequestStatus.FAILED);
            request.setStatusDetails(commonResult.getErrorMessage());
        }
        return request;
    }

    private ValidationResult validatePaymentEvent(RequestEvent requestEvent) {
        ValidationResult jakartaValidationResult = jakartaValidator.validate(requestEvent);
        ValidationResult requestTypeResult = validator.validateRequestTypeTransferTo(requestEvent.getRequestType());
        ValidationResult operationTypeResult = validator.validateOperationTypeAuthorize(requestEvent.getOperationType());
        ValidationResult sentTimeNotOlderResult = validator.validateSentTimeNotOlderThan(requestEvent.getSentDateTime(), SENT_TIME_PERIOD_IN_MINUTES);
        ValidationResult accountExistsResult = validator.validateAccountExists(requestEvent.getAccountId());

        Account account = accountRepository.findById(requestEvent.getAccountId()).orElseThrow(() ->
                new EntityNotFoundException(String.format("Account with id = %d not found", requestEvent.getAccountId())));
        ValidationResult accountActiveResult = validator.validateIfAccountActive(account);

        Balance balance = account.getBalance();
        ValidationResult sufficientBalanceResult = validator.validateSufficientActualBalance(
                balance.getActualBalance(), requestEvent.getAmount(), requestEvent.getAccountId());

        return ValidationResult.getCommonResult(
                jakartaValidationResult,
                requestTypeResult,
                operationTypeResult,
                sentTimeNotOlderResult,
                accountExistsResult,
                accountActiveResult,
                sufficientBalanceResult
        );
    }
}