package faang.school.accountservice.validator;

import faang.school.accountservice.model.entity.Account;
import faang.school.accountservice.model.enums.AccountStatus;
import faang.school.accountservice.model.enums.OperationType;
import faang.school.accountservice.model.enums.RequestStatus;
import faang.school.accountservice.model.enums.RequestType;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.RequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
public class PaymentEventValidator {
    private final AccountRepository accountRepository;

    public ValidationResult validateSentTimeNotOlderThan(LocalDateTime sentDateTime, int sentTimePeriodInMinutes) {
        LocalDateTime now = LocalDateTime.now();
        long minutesDifference = ChronoUnit.MINUTES.between(sentDateTime, now);

        if (minutesDifference > sentTimePeriodInMinutes) {
            return ValidationResult.failure(String.format("Sent date and time is older than %d minutes", sentTimePeriodInMinutes));
        }

        return ValidationResult.success();
    }


    public ValidationResult validateAccountExists(Long accountId) {
        if (!accountRepository.existsById(accountId)) {
            return ValidationResult.failure(String.format("Account with id = %d doesn't exist", accountId));
        }

        return ValidationResult.success();
    }

    public ValidationResult validateIfAccountActive(Account account) {
        if (account.getStatus() != AccountStatus.ACTIVE) {
            return ValidationResult.failure("Account should be in ACTIVE status");
        }

        return ValidationResult.success();
    }

    public ValidationResult validateSufficientActualBalance(BigDecimal actualBalance, BigDecimal amount, Long accountId) {
        if (actualBalance.compareTo(amount) < 0) {
            return ValidationResult.failure(String.format("Insufficient current balance for the account with id = %d", accountId));
        }

        return ValidationResult.success();
    }

    public ValidationResult validateRequestTypeTransferTo(RequestType requestType) {
        if (requestType != RequestType.TRANSFER_TO_USER && requestType != RequestType.TRANSFER_TO_PROJECT) {
            return ValidationResult.failure("In authorize RequestType should be TRANSFER_TO_USER or TRANSFER_TO_PROJECT");
        }

        return ValidationResult.success();
    }

    public ValidationResult validateOperationTypeAuthorize(OperationType operationType) {
        if (operationType != OperationType.AUTHORIZATION) {
            return ValidationResult.failure("In authorize OperationType should be AUTHORIZATION");
        }

        return ValidationResult.success();
    }
}
