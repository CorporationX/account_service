package faang.school.accountservice.dto;

import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.PaymentAccountStatus;
import faang.school.accountservice.enums.PaymentAccountType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PaymentAccountDto {
    private Long id;
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