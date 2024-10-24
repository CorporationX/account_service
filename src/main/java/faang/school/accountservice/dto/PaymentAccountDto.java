package faang.school.accountservice.dto;

import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.PaymentAccountStatus;
import faang.school.accountservice.enums.PaymentAccountType;
import faang.school.accountservice.validator.ValidOneOwner;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@ValidOneOwner
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PaymentAccountDto {
    private Long id;
    @Size(min = 12, max = 20, message = "min payment account number length  must be min 12 max 20")
    private String number;
    private Long projectId;
    private Long userId;
    private PaymentAccountType accountType;
    private Currency currency;
    private PaymentAccountStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime changedAt;
    private LocalDateTime closedAt;
}