package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.savingsaccount.SavingsAccountResponseDto;
import faang.school.accountservice.entity.SavingsAccount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface SavingsAccountMapper {

    @Mapping(target = "accountId", source = "account.id")
    @Mapping(target = "tariffId", source = "tariff.id")
    @Mapping(target = "createdAt", dateFormat = "dd.MM.yyyy HH:mm")
    @Mapping(target = "updatedAt", dateFormat = "dd.MM.yyyy HH:mm")
    SavingsAccountResponseDto toSavingsAccountResponseDto(SavingsAccount account);
}
