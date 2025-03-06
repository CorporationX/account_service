package faang.school.accountservice.dto;

import faang.school.accountservice.enums.AccountOwnerType;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import lombok.Data;

import java.math.BigInteger;

@Data
public class AccountFilterDto {
    private String accountNumber;
    private BigInteger ownerId;
    private AccountOwnerType ownerType;
    private AccountType type;
    private AccountStatus accountStatus;
}
