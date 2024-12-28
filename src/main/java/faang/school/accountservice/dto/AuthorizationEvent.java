package faang.school.accountservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorizationEvent {
    private Long recipientAccountId;
    private BigDecimal amount;
    private Long senderAccountId;
    private String verificationCode;
}