package faang.school.accountservice.service.impl;

import faang.school.accountservice.exception.DataValidationException;
import faang.school.accountservice.model.entity.Account;
import faang.school.accountservice.model.entity.Balance;
import faang.school.accountservice.model.entity.Request;
import faang.school.accountservice.model.enums.OperationType;
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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentRequestServiceImpl implements PaymentRequestService {
    private static final int SENT_TIME_PERIOD_IN_MINUTES = 15;
    private static final long CACHE_EXPIRATION_TIME_IN_MINUTES = 5;

    private final RedisTemplate<String, PaymentEvent> redisTemplate;
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
        if (checkEventMessageRepeated(paymentEvent)) return;

        ValidationResult commonResult = validateAuthorizePaymentEvent(paymentEvent);
        if (!commonResult.isValid()) {
            Request request = createRequest(paymentEvent, commonResult);
            requestRepository.save(request);
            return;
        }

        boolean inProgressRequestExists = requestRepository.existsByAccountIdAndStatus(paymentEvent.getSenderAccountId(), RequestStatus.IN_PROGRESS);
        if (inProgressRequestExists) {
            log.debug(String.format("An active payment request already exists for senderAccountId = %d; ignoring duplicate request", paymentEvent.getSenderAccountId()));
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
        if (checkEventMessageRepeated(paymentEvent)) return;
        ValidationResult commonResult = validateCancelPaymentEvent(paymentEvent);
        if (!commonResult.isValid()) {
            Request request = requestRepository.findById(paymentEvent.getRequestId()).orElseThrow(() ->
                    new IllegalStateException(String.format("Expected request with id = %d to be present because there " +
                            "was payment event from redis, but it was not found", paymentEvent.getRequestId())));
            Map<String, Object> inputData = request.getInputData();
            inputData.put("errorWhileCancel", commonResult.getErrorMessage());
            requestRepository.save(request);
            return;
        }

        Optional<Request> existingRequestOpt = requestRepository.findByIdempotencyToken(paymentEvent.getIdempotencyToken());
        if (existingRequestOpt.isPresent()) {
            Request existingRequest = existingRequestOpt.get();

            switch (existingRequest.getStatus()) {
                case CANCELLED:
                    return;
                case IN_PROGRESS:
                case FAILED:
                    Request request = cancelRequest(existingRequest, paymentEvent);
                    //TODO  добавить createAuditRecord? или тут уже не надо
                    applicationEventPublisher.publishEvent(new PaymentStatusEvent(request.getId(), request.getIdempotencyToken(), request.getStatus()));
                    return;
                case COMPLETED:
                    Map<String, Object> inputData = existingRequest.getInputData();
                    inputData.put("cancelSentDateTime", paymentEvent.getSentDateTime());
                    inputData.put("additionalInfo", "Attempt to cancel COMPLETED request");
                    existingRequest.setInputData(inputData);
                    return;
                default:
                    throw new DataValidationException(String.format("Unexpected request status: %s", existingRequest.getStatus()));
            }
        }
    }

    @Override
    @Transactional
    public void clearing(PaymentEvent paymentEvent) {
        if (checkEventMessageRepeated(paymentEvent)) return;
        ValidationResult commonResult = validateClearingPaymentEvent(paymentEvent);
        if (!commonResult.isValid()) {
            Request request = requestRepository.findById(paymentEvent.getRequestId()).orElseThrow(() ->
                    new IllegalStateException(String.format("Expected request with id = %d to be present because there " +
                            "was payment event from redis, but it was not found", paymentEvent.getRequestId())));
            Map<String, Object> inputData = request.getInputData();
            inputData.put("errorWhileClearing", commonResult.getErrorMessage());
            requestRepository.save(request);
            return;
        }

        Optional<Request> existingRequestOpt = requestRepository.findByIdempotencyToken(paymentEvent.getIdempotencyToken());
        if (existingRequestOpt.isPresent()) {
            Request existingRequest = existingRequestOpt.get();
            Map<String, Object> inputData = existingRequest.getInputData();

            switch (existingRequest.getStatus()) {
                case CANCELLED:
                    inputData.put("clearSentDateTime", paymentEvent.getSentDateTime());
                    inputData.put("additionalInfo", "Attempt to clearing CANCELLED request");
                    existingRequest.setInputData(inputData);
                    return;
                case IN_PROGRESS:
                    Request request = clearRequest(existingRequest, paymentEvent);
                    //TODO  добавить createAuditRecord? или тут уже не надо
                    applicationEventPublisher.publishEvent(new PaymentStatusEvent(request.getId(), request.getIdempotencyToken(), request.getStatus()));
                    return;
                case FAILED:
                    inputData.put("clearSentDateTime", paymentEvent.getSentDateTime());
                    inputData.put("additionalInfo", "Attempt to clearing FAILED request");
                    existingRequest.setInputData(inputData);
                    return;
                case COMPLETED:
                    return;
                default:
                    throw new DataValidationException(String.format("Unexpected request status: %s", existingRequest.getStatus()));
            }
        }

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
        }
        inputData.put("lastSentDateTime", paymentEvent.getSentDateTime());
        existingRequest.setInputData(inputData);

        Account senderAccount = accountRepository.findById(paymentEvent.getSenderAccountId()).orElseThrow(() ->
                new IllegalStateException(String.format("Expected account with id = %d to be present because validate " +
                        "payment event already done without mistakes, but it was not found", paymentEvent.getSenderAccountId())));
        Balance balance = senderAccount.getBalance();
        balance.setAuthorizedBalance(paymentEvent.getAmount());
        balance.setActualBalance(balance.getActualBalance().subtract(paymentEvent.getAmount()));
        balanceRepository.save(balance);
        existingRequest.setSenderAccount(senderAccount);
        existingRequest.setStatus(RequestStatus.IN_PROGRESS);
        existingRequest.setStatusDetails(null);
        Account recipientAccount = accountRepository.findById(paymentEvent.getRecipientAccountId()).orElseThrow(() ->
                new IllegalStateException(String.format("Expected recipient account with id = %d to be present because there " +
                        "was payment event from redis, but it was not found", paymentEvent.getRecipientAccountId())));
        existingRequest.setRecipientAccount(recipientAccount);
        return requestRepository.save(existingRequest);
    }

    private Request createRequest(PaymentEvent paymentEvent, ValidationResult commonResult) {
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("amount", paymentEvent.getAmount());
        inputData.put("firstSentDateTime", paymentEvent.getSentDateTime());
        inputData.put("lastSentDateTime", paymentEvent.getSentDateTime());

        Optional<Account> senderAccountOptional = accountRepository.findById(paymentEvent.getSenderAccountId());

        Request request = new Request();
        request.setIdempotencyToken(paymentEvent.getIdempotencyToken());
        request.setRequestType(paymentEvent.getRequestType());
        request.setOperationType(paymentEvent.getOperationType());
        request.setInputData(inputData);

        if (commonResult.isValid() && senderAccountOptional.isPresent()) {
            Account senderAccount = senderAccountOptional.get();
            Balance balance = senderAccount.getBalance();
            balance.setAuthorizedBalance(paymentEvent.getAmount());
            balance.setActualBalance(balance.getActualBalance().subtract(paymentEvent.getAmount()));
            balanceRepository.save(balance);

            request.setSenderAccount(senderAccount);
            request.setStatus(RequestStatus.IN_PROGRESS);
            request.setStatusDetails(null);
            Account recipientAccount = accountRepository.findById(paymentEvent.getRecipientAccountId()).orElseThrow(() ->
                    new IllegalStateException(String.format("Expected recipient account with id = %d to be present because there " +
                            "was payment event from redis, but it was not found", paymentEvent.getRecipientAccountId())));
            request.setRecipientAccount(recipientAccount);
        } else {
            request.setStatus(RequestStatus.FAILED);
            request.setStatusDetails(commonResult.getErrorMessage());
        }
        return request;
    }

    private ValidationResult validateAuthorizePaymentEvent(PaymentEvent paymentEvent) {
        List<ValidationResult> validationResults = new ArrayList<>();
        validationResults.add(jakartaValidator.validate(paymentEvent, PaymentEvent.AuthorizePaymentEventMarker.class));
        validationResults.add(validator.validateRequestTypeTransferTo(paymentEvent.getRequestType()));
        validationResults.add(validator.validateOperationType(paymentEvent.getOperationType(), OperationType.AUTHORIZATION));
        validationResults.add(validator.validateSentTimeNotOlderThan(paymentEvent.getSentDateTime(), SENT_TIME_PERIOD_IN_MINUTES));

        Optional<Account> senderAccountOpt = accountRepository.findById(paymentEvent.getSenderAccountId());
        if (senderAccountOpt.isEmpty()) {
            validationResults.add(ValidationResult.failure(String.format("Sender account with id = %d not found", paymentEvent.getSenderAccountId())));
        } else {
            Account senderAccount = senderAccountOpt.get();
            validationResults.add(validator.validateIfAccountActive(senderAccount));

            Balance balance = senderAccount.getBalance();
            validationResults.add(validator.validateSufficientActualBalance(
                    balance.getActualBalance(), paymentEvent.getAmount(), paymentEvent.getSenderAccountId()));
        }

        addAccountValidations(validationResults, paymentEvent.getRecipientAccountId());
        return ValidationResult.getCommonResult(validationResults);
    }

    private ValidationResult validateCancelPaymentEvent(PaymentEvent paymentEvent) {
        List<ValidationResult> validationResults = new ArrayList<>();
        validationResults.add(jakartaValidator.validate(paymentEvent, PaymentEvent.CancelPaymentEventMarker.class));
        validationResults.add(validator.validateOperationType(paymentEvent.getOperationType(), OperationType.CANCEL));
        validationResults.add(validator.validateSentTimeNotOlderThan(paymentEvent.getSentDateTime(), SENT_TIME_PERIOD_IN_MINUTES));
        addAccountValidations(validationResults, paymentEvent.getSenderAccountId());
        addAccountValidations(validationResults, paymentEvent.getRecipientAccountId());
        return ValidationResult.getCommonResult(validationResults);
    }

    private ValidationResult validateClearingPaymentEvent(PaymentEvent paymentEvent) {
        List<ValidationResult> validationResults = new ArrayList<>();
        validationResults.add(jakartaValidator.validate(paymentEvent, PaymentEvent.ClearingPaymentEventMarker.class));
        validationResults.add(validator.validateOperationType(paymentEvent.getOperationType(), OperationType.CLEARING));
        validationResults.add(validator.validateSentTimeNotOlderThan(paymentEvent.getSentDateTime(), SENT_TIME_PERIOD_IN_MINUTES));
        addAccountValidations(validationResults, paymentEvent.getSenderAccountId());
        addAccountValidations(validationResults, paymentEvent.getRecipientAccountId());
        return ValidationResult.getCommonResult(validationResults);
    }

    private void addAccountValidations(List<ValidationResult> validationResults, Long accountId) {
        Optional<Account> accountOpt = accountRepository.findById(accountId);
        if (accountOpt.isEmpty()) {
            validationResults.add(ValidationResult.failure(String.format("Account with id = %d not found", accountId)));
        } else {
            Account account = accountOpt.get();
            validationResults.add(validator.validateIfAccountActive(account));
        }
    }

    private Request cancelRequest(Request existingRequest, PaymentEvent paymentEvent) {
        Map<String, Object> inputData = existingRequest.getInputData();
        inputData.put("cancelSentDateTime", paymentEvent.getSentDateTime());
        existingRequest.setInputData(inputData);

        Account senderAccount = existingRequest.getSenderAccount();
        Balance balance = senderAccount.getBalance();
        balance.setAuthorizedBalance(BigDecimal.ZERO);
        balance.setActualBalance(balance.getActualBalance().add(paymentEvent.getAmount()));
        balanceRepository.save(balance);
        existingRequest.setStatus(RequestStatus.CANCELLED);
        return requestRepository.save(existingRequest);
    }

    private Request clearRequest(Request existingRequest, PaymentEvent paymentEvent) {
        Map<String, Object> inputData = existingRequest.getInputData();
        inputData.put("clearSentDateTime", paymentEvent.getSentDateTime());
        existingRequest.setInputData(inputData);

        Account senderAccount = existingRequest.getSenderAccount();
        Balance senderBalance = senderAccount.getBalance();
        Account receiverAccount = existingRequest.getRecipientAccount();
        Balance receiverBalance = receiverAccount.getBalance();
        receiverBalance.setActualBalance(receiverBalance.getActualBalance().add(senderBalance.getAuthorizedBalance()));
        balanceRepository.save(receiverBalance);
        senderBalance.setAuthorizedBalance(BigDecimal.ZERO);
        balanceRepository.save(senderBalance);
        existingRequest.setStatus(RequestStatus.COMPLETED);
        return requestRepository.save(existingRequest);
    }

    private String generateCacheKey(PaymentEvent paymentEvent) {
        return String.format("paymentEvent: %s:%s:%s:%s:%s",
                paymentEvent.getIdempotencyToken(),
                paymentEvent.getSenderAccountId(),
                paymentEvent.getRecipientAccountId(),
                paymentEvent.getRequestType(),
                paymentEvent.getOperationType());
    }

    private boolean checkEventMessageRepeated(PaymentEvent paymentEvent) {
        ValueOperations<String, PaymentEvent> valueOps = redisTemplate.opsForValue();
        String cacheKey = generateCacheKey(paymentEvent);

        if (valueOps.get(cacheKey) != null) {
            log.debug("Duplicate paymentEvent detected for idempotencyToken: {}; ignoring it", paymentEvent.getIdempotencyToken());
            return true;
        }

        valueOps.set(cacheKey, paymentEvent, CACHE_EXPIRATION_TIME_IN_MINUTES, TimeUnit.MINUTES);
        return false;
    }
}