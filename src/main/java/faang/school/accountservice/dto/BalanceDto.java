package faang.school.accountservice.dto;

import lombok.Data;

@Data
public class BalanceDto {
    private long id;
    private long accountId;
    private double curAuthBalance;
    private double curFactBalance;
    private int version;


    public void nextVersion() {
        this.version++;
    }
}
