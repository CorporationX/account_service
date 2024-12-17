package faang.school.accountservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentOperationDto {
    private Long id;
    private BigDecimal amount; // сумма
    private String currency; // валюта
    private long ownerId; // счет отправителя (проверку что такой счет существует)
    private long recipientId; // счет получателя (проверка что счет корректен)
    private PaymentOperationType operationType;
    private PaymentStatus status;
//    private LocalDateTime clearScheduledAt;
    private LocalDateTime createdAt; //дата создания
    private LocalDateTime updatedAt; //дата обновления
}