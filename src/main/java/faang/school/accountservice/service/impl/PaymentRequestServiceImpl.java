package faang.school.accountservice.service.impl;

import faang.school.accountservice.exception.DataValidationException;
import faang.school.accountservice.model.entity.Account;
import faang.school.accountservice.model.entity.Balance;
import faang.school.accountservice.model.entity.Request;
import faang.school.accountservice.model.enums.RequestStatus;
import faang.school.accountservice.model.event.PaymentEvent;
import faang.school.accountservice.model.event.PaymentStatusEvent;
import faang.school.accountservice.publisher.PaymentStatusEventPublisher;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.BalanceRepository;
import faang.school.accountservice.repository.RequestRepository;
import faang.school.accountservice.service.PaymentRequestService;
import faang.school.accountservice.validator.JakartaValidator;
import faang.school.accountservice.validator.PaymentEventValidator;
import faang.school.accountservice.validator.ValidationResult;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    public void authorize(PaymentEvent paymentEvent) {
        ValidationResult commonResult = validatePaymentEvent(paymentEvent);
        if (!commonResult.isValid()) {
            Request request = createRequest(paymentEvent, commonResult);
            requestRepository.save(request);
            return;
        }

        boolean activeRequestExists = requestRepository.existsByAccountIdAndStatus(paymentEvent.getAccountId(), RequestStatus.IN_PROGRESS);

        if (activeRequestExists) {
            log.debug(String.format("An active payment request already exists for accountId = %d; ignoring duplicate request", paymentEvent.getAccountId()));
            return;
        }

        Optional<Request> existingRequestOpt = requestRepository.findByIdempotencyToken(paymentEvent.getIdempotencyToken());

        if (existingRequestOpt.isPresent()) {
            Request existingRequest = existingRequestOpt.get();

            switch (existingRequest.getStatus()) {
                case IN_PROGRESS:
                    log.debug(String.format("The request with id = %d, idempotencyToken = %s is already being processed; ignoring it",
                            existingRequest.getId(), existingRequest.getIdempotencyToken()));
                    return;

                case FAILED:
                    log.debug(String.format("The request with id = %d, idempotencyToken = %s previously failed; retrying it",
                            existingRequest.getId(), existingRequest.getIdempotencyToken()));

                    Request request = retryFailedRequest(existingRequest, paymentEvent);
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

        Request request = createRequest(paymentEvent, commonResult);
        Request savedRequest = requestRepository.save(request);
        //TODO  добавить createAuditRecord
        applicationEventPublisher.publishEvent(new PaymentStatusEvent(savedRequest.getId(), savedRequest.getIdempotencyToken(), savedRequest.getStatus()));
    }

    @Override
    @Transactional
    public void cancel(PaymentEvent paymentEvent) {
        //статус можно перевести в CANCELED если он IN_PROGRESS или FAILED
        //если он уже CANCELED, то просто выходим
        //если он уже в статусе COMPLETED, надо как-то сообщить, что уже поздно отменять
        Optional<Request> existingRequestOpt = requestRepository.findByIdempotencyToken(paymentEvent.getIdempotencyToken());

        if (existingRequestOpt.isPresent()) {
            Request existingRequest = existingRequestOpt.get();

            switch (existingRequest.getStatus()) {
                case CANCELLED:
                    return;

                case IN_PROGRESS:
                case FAILED:
                    Request request = cancelRequest();
                    //TODO  добавить createAuditRecord? или тут уже не надо
                    if (request.getStatus() == RequestStatus.IN_PROGRESS) {
                        applicationEventPublisher.publishEvent(new PaymentStatusEvent(request.getId(), request.getIdempotencyToken(), request.getStatus()));
                    }
                    return;

//                case IN_PROGRESS:
//                    log.debug(String.format("The request with id = %d, idempotencyToken = %s is already being processed; ignoring it",
//                            existingRequest.getId(), existingRequest.getIdempotencyToken()));
//                    return;

//                case FAILED:
//                    log.debug(String.format("The request with id = %d, idempotencyToken = %s previously failed; retrying it",
//                            existingRequest.getId(), existingRequest.getIdempotencyToken()));
//                    Request request = retryFailedRequest(existingRequest, paymentEvent);
//                    TODO  добавить createAuditRecord
//                    if (request.getStatus() == RequestStatus.IN_PROGRESS) {
//                        applicationEventPublisher.publishEvent(new PaymentStatusEvent(request.getId(), request.getIdempotencyToken(), request.getStatus()));
//                    }
//                    return;
//
//                case COMPLETED:
//                case CANCELLED:
//                    log.debug(String.format("The request with id = %d, idempotencyToken = %s is already completed/canceled; not executing it again",
//                            existingRequest.getId(), existingRequest.getIdempotencyToken()));
//                    return;

                default:
                    throw new DataValidationException(String.format("Unexpected request status: %s", existingRequest.getStatus()));
            }
        }


    }

    @Override
    @Transactional
    public void clearing(PaymentEvent paymentEvent) {

    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onRequestCompleted(PaymentStatusEvent event) {
        paymentStatusEventPublisher.publish(event);
    }

    private Request retryFailedRequest(Request existingRequest, PaymentEvent paymentEvent) {
        Map<String, Object> inputData = existingRequest.getInputData();

        if (inputData.containsKey("amount") && !inputData.get("amount").equals(paymentEvent.getAmount())) {
            log.debug("Updating amount in request for retry. Old amount: " + inputData.get("amount") + ", new amount: " + paymentEvent.getAmount());
            inputData.put("amount", paymentEvent.getAmount());
            inputData.put("lastSentDateTime", paymentEvent.getSentDateTime());
            existingRequest.setInputData(inputData);
        }

        Account account = accountRepository.findById(paymentEvent.getAccountId()).orElseThrow(() ->
                new IllegalStateException(String.format("Expected account with id = %d to be present because validate " +
                        "payment event already done without mistakes, but it was not found", paymentEvent.getAccountId())));
        Balance balance = account.getBalance();
        balance.setAuthorizedBalance(paymentEvent.getAmount());
        balance.setActualBalance(balance.getActualBalance().subtract(paymentEvent.getAmount()));
        balanceRepository.save(balance);
        existingRequest.setStatus(RequestStatus.IN_PROGRESS);

        return requestRepository.save(existingRequest);
    }

    private Request createRequest(PaymentEvent paymentEvent, ValidationResult commonResult) {
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("amount", paymentEvent.getAmount());
        inputData.put("firstSentDateTime", paymentEvent.getSentDateTime());
        inputData.put("lastSentDateTime", paymentEvent.getSentDateTime());

        Optional<Account> accountOptional = accountRepository.findById(paymentEvent.getAccountId());

        Request request = new Request();
        request.setIdempotencyToken(paymentEvent.getIdempotencyToken());
        request.setRequestType(paymentEvent.getRequestType());
        request.setOperationType(paymentEvent.getOperationType());
        request.setInputData(inputData);

        if (commonResult.isValid() && accountOptional.isPresent()) {
            Account account = accountOptional.get();
            Balance balance = account.getBalance();
            balance.setAuthorizedBalance(paymentEvent.getAmount());
            balance.setActualBalance(balance.getActualBalance().subtract(paymentEvent.getAmount()));
            balanceRepository.save(balance);

            request.setAccount(account);
            request.setStatus(RequestStatus.IN_PROGRESS);
        } else {
            request.setStatus(RequestStatus.FAILED);
            request.setStatusDetails(commonResult.getErrorMessage());
        }
        return request;
    }

    private ValidationResult validatePaymentEvent(PaymentEvent paymentEvent) {
        List<ValidationResult> validationResults = new ArrayList<>();
        validationResults.add(jakartaValidator.validate(paymentEvent, PaymentEvent.AuthorizePaymentEventMarker.class));
        validationResults.add(validator.validateRequestTypeTransferTo(paymentEvent.getRequestType()));
        validationResults.add(validator.validateOperationTypeAuthorize(paymentEvent.getOperationType()));
        validationResults.add(validator.validateSentTimeNotOlderThan(paymentEvent.getSentDateTime(), SENT_TIME_PERIOD_IN_MINUTES));

        Optional<Account> accountOpt = accountRepository.findById(paymentEvent.getAccountId());
        if (accountOpt.isEmpty()) {
            validationResults.add(ValidationResult.failure(String.format("Account with id = %d not found", paymentEvent.getAccountId())));
        } else {
            Account account = accountOpt.get();

            validationResults.add(validator.validateIfAccountActive(account));

            Balance balance = account.getBalance();
            validationResults.add(validator.validateSufficientActualBalance(
                    balance.getActualBalance(), paymentEvent.getAmount(), paymentEvent.getAccountId()));
        }

        return ValidationResult.getCommonResult(validationResults);
    }

    private Request cancelRequest(Request existingRequest, PaymentEvent paymentEvent) {
        Map<String, Object> inputData = existingRequest.getInputData();
//надо балансы вернуть как были
        if (inputData.containsKey("amount") && !inputData.get("amount").equals(paymentEvent.getAmount())) {
            log.debug("Updating amount in request for retry. Old amount: " + inputData.get("amount") + ", new amount: " + paymentEvent.getAmount());
            inputData.put("amount", paymentEvent.getAmount());
            inputData.put("lastSentDateTime", paymentEvent.getSentDateTime());
            existingRequest.setInputData(inputData);
        }

        ValidationResult retryResult = validatePaymentEvent(paymentEvent);
        if (retryResult.isValid()) {
            existingRequest.setStatus(RequestStatus.IN_PROGRESS);
        } else {
            existingRequest.setStatus(RequestStatus.FAILED);
            existingRequest.setStatusDetails(retryResult.getErrorMessage());
        }

        return requestRepository.save(existingRequest);
    }
}