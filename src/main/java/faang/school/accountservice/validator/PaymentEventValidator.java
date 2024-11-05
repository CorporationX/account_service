package faang.school.accountservice.validator;

import faang.school.accountservice.model.entity.Account;
import faang.school.accountservice.model.enums.AccountStatus;
import faang.school.accountservice.model.enums.OperationType;
import faang.school.accountservice.model.enums.RequestType;
import faang.school.accountservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
public class PaymentEventValidator {
    private final AccountRepository accountRepository;

    //возможно нет смысла в этой проверке
    public ValidationResult validateSentTimeNotOlderThan(LocalDateTime sentDateTime, int sentTimePeriodInMinutes) {
        LocalDateTime now = LocalDateTime.now();
        long minutesDifference = ChronoUnit.MINUTES.between(sentDateTime, now);

        if (minutesDifference > sentTimePeriodInMinutes) {
            return ValidationResult.failure(String.format("Sent date and time is older than %d minutes", sentTimePeriodInMinutes));
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

    public ValidationResult validateOperationType(OperationType operationType, OperationType expectedOperationType) {
        if (operationType != expectedOperationType) {
            return ValidationResult.failure(String.format("In authorize OperationType should be %s", expectedOperationType));
        }

        return ValidationResult.success();
    }
}
