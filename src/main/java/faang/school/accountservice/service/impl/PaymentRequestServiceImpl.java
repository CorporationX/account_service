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
import faang.school.accountservice.service.BalanceAuditService;
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
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    private static final long CACHE_EXPIRATION_TIME_IN_MINUTES = 1;

    private final RedisTemplate<String, Object> redisTemplate;
    private final AccountRepository accountRepository;
    private final BalanceRepository balanceRepository;
    private final RequestRepository requestRepository;
    private final JakartaValidator jakartaValidator;
    private final PaymentEventValidator validator;
    private final PaymentStatusEventPublisher paymentStatusEventPublisher;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final BalanceAuditService balanceAuditService;

    @Override
    @Transactional
    public void authorize(PaymentEvent paymentEvent) {
        if (checkEventMessageRepeated(paymentEvent)) return;

        ValidationResult commonResult = validateAuthorizePaymentEvent(paymentEvent);
        if (!commonResult.isValid()) {
            Request request = createRequest(paymentEvent, commonResult);
            Request savedRequest = requestRepository.save(request);
            publishPaymentStatusEvent(savedRequest, paymentEvent.getSentDateTime());
            return;
        }

        boolean inProgressRequestExists = requestRepository.existsBySenderAccountIdAndStatus(paymentEvent.getSenderAccountId(), RequestStatus.IN_PROGRESS);
        if (inProgressRequestExists) {
            log.debug(String.format("An IN_PROGRESS payment request already exists for senderAccountId = %d; ignoring duplicate request", paymentEvent.getSenderAccountId()));
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

                    Request savedRequest = retryFailedRequest(existingRequest, paymentEvent);
                    Account senderAccount = savedRequest.getSenderAccount();
                    balanceAuditService.save(senderAccount.getBalance(), savedRequest);
                    publishPaymentStatusEvent(savedRequest, paymentEvent.getSentDateTime());
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
        Account senderAccount = savedRequest.getSenderAccount();
        balanceAuditService.save(senderAccount.getBalance(), savedRequest);
        publishPaymentStatusEvent(savedRequest, paymentEvent.getSentDateTime());
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
            request.setStatusDetails(commonResult.getErrorMessage());
            Request savedRequest = requestRepository.save(request);
            publishPaymentStatusEvent(savedRequest, paymentEvent.getSentDateTime());
            return;
        }

        Request existingRequest = requestRepository.findByIdempotencyToken(paymentEvent.getIdempotencyToken()).orElseThrow(() ->
                new IllegalStateException(String.format("Attempt to CANCEL payment request with idempotencyToken = %s, " +
                        "which not found", paymentEvent.getIdempotencyToken())));

        switch (existingRequest.getStatus()) {
            case CANCELLED:
                return;
            case IN_PROGRESS:
            case FAILED:
                Request savedRequest = cancelRequest(existingRequest, paymentEvent);
                Account senderAccount = savedRequest.getSenderAccount();
                balanceAuditService.save(senderAccount.getBalance(), savedRequest);
                publishPaymentStatusEvent(savedRequest, paymentEvent.getSentDateTime());
                return;
            case COMPLETED:
                existingRequest.setStatusDetails("Attempt to cancel COMPLETED request");
                requestRepository.save(existingRequest);
                publishPaymentStatusEvent(existingRequest, paymentEvent.getSentDateTime());
                return;
            default:
                throw new DataValidationException(String.format("Unexpected request status: %s", existingRequest.getStatus()));
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
            request.setStatusDetails(commonResult.getErrorMessage());
            Request savedRequest = requestRepository.save(request);
            publishPaymentStatusEvent(savedRequest, paymentEvent.getSentDateTime());
            return;
        }

        Request existingRequest = requestRepository.findByIdempotencyToken(paymentEvent.getIdempotencyToken()).orElseThrow(() ->
                new IllegalStateException(String.format("Attempt to CLEAR payment request with idempotencyToken = %s, " +
                        "which not found", paymentEvent.getIdempotencyToken())));
        Map<String, Object> inputData = existingRequest.getInputData();

        switch (existingRequest.getStatus()) {
            case CANCELLED:
                inputData.put("clearSentDateTime", paymentEvent.getSentDateTime());
                inputData.put("additionalInfo", "Attempt to clearing CANCELLED request");
                existingRequest.setInputData(inputData);
                return;
            case IN_PROGRESS:
                Request savedRequest = clearRequest(existingRequest, paymentEvent);
                Account senderAccount = savedRequest.getSenderAccount();
                balanceAuditService.save(senderAccount.getBalance(), savedRequest);
                Account recipientAccount = savedRequest.getRecipientAccount();
                balanceAuditService.save(recipientAccount.getBalance(), savedRequest);
                publishPaymentStatusEvent(savedRequest, paymentEvent.getSentDateTime());
                return;
            case FAILED:
                existingRequest.setStatusDetails("Attempt to clearing FAILED request");
                requestRepository.save(existingRequest);
                publishPaymentStatusEvent(existingRequest, paymentEvent.getSentDateTime());
                return;
            case COMPLETED:
                return;
            default:
                throw new DataValidationException(String.format("Unexpected request status: %s", existingRequest.getStatus()));
        }
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
        addAccountValidations(validationResults, paymentEvent.getSenderAccountId());
        addAccountValidations(validationResults, paymentEvent.getRecipientAccountId());
        return ValidationResult.getCommonResult(validationResults);
    }

    private ValidationResult validateClearingPaymentEvent(PaymentEvent paymentEvent) {
        List<ValidationResult> validationResults = new ArrayList<>();
        validationResults.add(jakartaValidator.validate(paymentEvent, PaymentEvent.ClearingPaymentEventMarker.class));
        validationResults.add(validator.validateOperationType(paymentEvent.getOperationType(), OperationType.CLEARING));
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
        existingRequest.setStatusDetails(null);
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
        ValueOperations<String, Object> valueOps = redisTemplate.opsForValue();
        String cacheKey = generateCacheKey(paymentEvent);

        if (valueOps.get(cacheKey) != null) {
            log.debug("Duplicate paymentEvent detected for idempotencyToken: {}; ignoring it", paymentEvent.getIdempotencyToken());
            return true;
        }

        valueOps.set(cacheKey, paymentEvent, CACHE_EXPIRATION_TIME_IN_MINUTES, TimeUnit.MINUTES);
        return false;
    }

    @Retryable(
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    private void publishPaymentStatusEvent(Request request, LocalDateTime eventSentDateTime) {
        applicationEventPublisher.publishEvent(new PaymentStatusEvent(
                request.getId(),
                request.getIdempotencyToken(),
                request.getStatus(),
                request.getStatusDetails(),
                eventSentDateTime));
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onRequestCompleted(PaymentStatusEvent event) {
        paymentStatusEventPublisher.publish(event);
    }
}