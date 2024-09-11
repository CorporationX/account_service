package faang.school.accountservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigInteger;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class BalanceDto {

    private long id;
    private long accountId;
    private BigInteger authBalance;
    private BigInteger currBalance;

}
