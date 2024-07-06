package faang.school.accountservice.dto;


import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OperationStatus;
import faang.school.accountservice.enums.OperationType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentOperationDto {
    @NotNull
    private UUID paymentId;

    @NotBlank
    private String senderAccountNumber;

    @NotBlank
    private String receiverAccountNumber;

    @Min(value = 1, message = "The minimal amount for a payment operation is 1 unit of currency.")
    @NotNull
    private BigDecimal amount;

    @NotNull
    private Currency currency;

    @NotNull
    private OperationType type;

    @NotNull
    private OperationStatus status;
}