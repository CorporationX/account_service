package faang.school.accountservice.service.auth_payment;


import faang.school.accountservice.dto.auth_payment.authorize.AuthorizationMessageRequest;
import faang.school.accountservice.entity.AuthPayment;
import faang.school.accountservice.enums.auth_payment.PaymentProcessingResult;
import faang.school.accountservice.exception.NonRetryableException;
import faang.school.accountservice.exception.non_retryable.CurrencyMismatchException;
import faang.school.accountservice.exception.non_retryable.EntityNotFoundException;
import faang.school.accountservice.exception.non_retryable.EqualAccountException;
import faang.school.accountservice.exception.non_retryable.NotActiveAccountException;
import faang.school.accountservice.exception.non_retryable.NotEnoughFundsException;
import faang.school.accountservice.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthPaymentValidator {
    private final AuthPaymentService authPaymentService;
    private final AccountService accountService;

    private static final Map<Class<? extends Exception>, PaymentProcessingResult> EXCEPTION_TO_RESULT = Map.of(
            NotEnoughFundsException.class, PaymentProcessingResult.NOT_ENOUGH_FUNDS,
            EntityNotFoundException.class, PaymentProcessingResult.ERROR,
            EqualAccountException.class, PaymentProcessingResult.ERROR,
            CurrencyMismatchException.class, PaymentProcessingResult.ERROR,
            NotActiveAccountException.class, PaymentProcessingResult.ERROR
    );

    public void validateEqualAccounts(AuthorizationMessageRequest request) throws NotActiveAccountException, EqualAccountException {
        accountService.checkAccountIsActive(request.senderAccountId());
        accountService.checkAccountIsActive(request.receiverAccountId());

        if (request.senderAccountId().equals(request.receiverAccountId())) {
            String error = String.format("Validation error. Equal accounts id payment request id %s",
                    request.id());
            log.warn(error);
            throw new EqualAccountException("Equal accounts. Process failed");
        }
    }

    public void validateAccountCurrencies(AuthorizationMessageRequest request) throws CurrencyMismatchException {
        accountService.checkCurrencyMismatch(request.senderAccountId(), request.currency());
        accountService.checkCurrencyMismatch(request.receiverAccountId(), request.currency());
    }

    public AuthPayment fetchPayment(UUID paymentId, Consumer<PaymentProcessingResult> responseSender) {
        try {
            return authPaymentService.getById(paymentId);
        } catch (EntityNotFoundException e) {
            log.error("Payment {} not found", paymentId);
            responseSender.accept(PaymentProcessingResult.PAYMENT_NOT_FOUND);
            return null;
        }
    }

    public boolean validatePayment(AuthPayment payment, Consumer<PaymentProcessingResult> responseSender) {
        return payment.getStatus().getProcessingResult()
                .map(result -> {
                    responseSender.accept(result);
                    return true;
                }).orElse(false);
    }

    public void handleValidationException(Exception e, UUID requestId, Consumer<PaymentProcessingResult> responseSender) {
        PaymentProcessingResult result = EXCEPTION_TO_RESULT.get(e.getClass());
        if (result != null) {
            log.error("Validation error: {} for request {}", e.getMessage(), requestId, e);
            responseSender.accept(result);
        } else {
            log.error("Unknown error in request id {}, sending to DLQ", requestId, e);
            throw new NonRetryableException(e.getMessage());
        }
    }
}
