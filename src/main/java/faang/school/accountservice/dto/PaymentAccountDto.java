package faang.school.accountservice.dto;

import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OwnerType;
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
    private String number;
    private Long ownerId;
    private OwnerType ownerType;
    private PaymentAccountType type;
    private Long balance;
    private Currency currency;
    private PaymentAccountStatus status;
    private LocalDateTime createDate;
    private LocalDateTime changeDate;
    private LocalDateTime closeDate;
}