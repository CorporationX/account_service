package faang.school.accountservice.service.transfer;


import faang.school.accountservice.dto.transfer_request.TransferRequestDto;
import faang.school.accountservice.entity.TransferRequest;
import faang.school.accountservice.enums.transfer_request.TransferStatus;
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
public class TransferValidator {
    private final TransferService transferService;
    private final AccountService accountService;

    private final Map<Class<? extends Exception>, TransferStatus> exceptionToResult = Map.of(
            NotEnoughFundsException.class, TransferStatus.NOT_ENOUGH_FUNDS,
            EntityNotFoundException.class, TransferStatus.ERROR,
            EqualAccountException.class, TransferStatus.ERROR,
            CurrencyMismatchException.class, TransferStatus.ERROR,
            NotActiveAccountException.class, TransferStatus.ERROR
    );

    public void validateAccounts(TransferRequestDto request) throws NotActiveAccountException, EqualAccountException {
        accountService.checkAccountIsActive(request.senderAccountNumber());
        accountService.checkAccountIsActive(request.receiverAccountNumber());
        accountService.checkCurrencyMismatch(request.senderAccountNumber(), request.currency());
        accountService.checkCurrencyMismatch(request.receiverAccountNumber(), request.currency());

        if (request.senderAccountNumber().equals(request.receiverAccountNumber())) {
            String error = String.format("Validation error. Equal accounts id transfer request id %s", request.id());
            log.warn(error);
            throw new EqualAccountException("Equal accounts. Process failed");
        }
    }

    public TransferRequest getTransferOrThrow(UUID paymentId, Consumer<TransferStatus> responseSender) {
        try {
            return transferService.getById(paymentId);
        } catch (EntityNotFoundException e) {
            log.error("Payment {} not found", paymentId);
            responseSender.accept(TransferStatus.PAYMENT_NOT_FOUND);
            return null;
        }
    }

    public void handleValidationException(Exception e, UUID requestId, Consumer<TransferStatus> responseSender) {
        TransferStatus result = exceptionToResult.get(e.getClass());
        if (result != null) {
            log.error("Validation error: {} for request {}", e.getMessage(), requestId, e);
            responseSender.accept(result);
        } else {
            log.error("Unknown error in request id {}, sending to DLQ", requestId, e);
            throw new NonRetryableException(e.getMessage());
        }
    }
}
