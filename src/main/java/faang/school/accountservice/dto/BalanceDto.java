package faang.school.accountservice.dto;

import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class BalanceDto {
    @Positive
    private long id;
    @Positive
    private long accountId;
    private double curAuthBalance;
    private double curFactBalance;

    @Positive
    private int version;


    public void nextVersion() {
        this.version++;
    }
}
