package faang.school.accountservice.dto.balance;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class BalanceCreateResponseDto {
    private Long id;
    private String accountNumber;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal authorisationBalance;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal factualBalance;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
