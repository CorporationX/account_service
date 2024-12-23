package faang.school.accountservice.service.validation;

import faang.school.accountservice.dto.PaymentOperationDto;
import faang.school.accountservice.dto.PaymentValidationResult;
import faang.school.accountservice.enums.AccValidationStatus;
import faang.school.accountservice.service.account.AccountService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ValidationService {
    private final AccountService accountService;

    public PaymentValidationResult validateAccounts(PaymentOperationDto payload) {
        try {
            if (!accountService.existsAccById(payload.getOwnerAccId())) {
                return new PaymentValidationResult(
                        AccValidationStatus.ERROR,
                        new EntityNotFoundException("Owner account not found: " + payload.getOwnerAccId())
                );
            }
            if (!accountService.existsAccById(payload.getRecipientAccId())) {
                return new PaymentValidationResult(
                        AccValidationStatus.ERROR,
                        new EntityNotFoundException("Recipient account not found: " + payload.getRecipientAccId())
                );
            }
            return new PaymentValidationResult(AccValidationStatus.SUCCESS, null);
        } catch (Exception e) {
            return new PaymentValidationResult(AccValidationStatus.ERROR, e);
        }
    }
}
