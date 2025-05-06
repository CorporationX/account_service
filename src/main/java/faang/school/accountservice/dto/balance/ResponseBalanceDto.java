package faang.school.accountservice.dto.balance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ResponseBalanceDto {
    private long id;
    private long accountId;
    private double authorizationBalance;
    private double actualBalance;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
}
