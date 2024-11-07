package faang.school.accountservice.mapper;

import faang.school.accountservice.enums.account.AccountType;
import faang.school.accountservice.model.FreeAccountNumber;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FreeAccountNumberMapper {
    default FreeAccountNumber toFreeAccountNumber(AccountType accountType, Long currentNumber, int numberLength) {
        String accountNumber = accountType.getCode() + String.format("%0" + numberLength + "d", currentNumber);
        return FreeAccountNumber.builder()
                .accountNumber(accountNumber)
                .accountType(accountType)
                .build();
    }
}