package faang.school.accountservice.dto.Balance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReturnedBalanceDto {
    private long id;
    private double authorizationBalance;
    private double actualBalance;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int version;
    private long accountId;
}
