package faang.school.accountservice.mapper;

import faang.school.accountservice.enums.account.AccountType;
import faang.school.accountservice.model.FreeAccountNumber;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.math.BigInteger;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FreeAccountNumberMapper {
    default FreeAccountNumber toFreeAccountNumber(AccountType accountType, BigInteger accountNumber) {
        return FreeAccountNumber.builder()
                .id(new FreeAccountNumber.FreeAccountNumberKey(accountType.name(), accountNumber))
                .accountType(accountType.name())
                .accountNumber(accountNumber)
                .build();
    }
}