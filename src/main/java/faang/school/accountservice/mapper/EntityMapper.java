package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.SavingsAccountDto;
import faang.school.accountservice.dto.TariffDto;
import faang.school.accountservice.model.SavingsAccount;
import faang.school.accountservice.model.Tariff;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EntityMapper {
    @Mapping(source = "account.id", target = "id")
    @Mapping(source = "tariffHistory", target = "tariffHistory")
    @Mapping(source = "lastInterestCalculatedDate", target = "lastInterestCalculatedDate")
    @Mapping(source = "version", target = "version")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    SavingsAccountDto toDto(SavingsAccount savingsAccount);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "rateHistory", target = "rateHistory")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    TariffDto toDto(Tariff tariff);
}
