package faang.school.accountservice.dto;



import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record BalanceUpdateCommand(
        BigDecimal currentBalanceDelta,
        BigDecimal availableBalanceDelta
) {

    public BalanceUpdateCommand {
        if (currentBalanceDelta == null || availableBalanceDelta == null) {
            throw new IllegalArgumentException("Значения не может быть пустым");
        }

    }
}
