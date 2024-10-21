package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.savings_account.SavingsAccountCreateDto;
import faang.school.accountservice.dto.savings_account.SavingsAccountResponseDto;
import faang.school.accountservice.model.SavingsAccount;
import faang.school.accountservice.model.enumeration.TariffType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SavingsAccountMapper {
    SavingsAccount toEntity(SavingsAccountCreateDto dto);

    @Mapping(target = "number", source = "entity.account.number")
    @Mapping(target = "owner", source = "entity.account.owner")
    @Mapping(target = "currency", source = "entity.account.currency")
    @Mapping(target = "amount", source = "entity.account.amount")
    @Mapping(target = "tariffType", source = "entity", qualifiedByName = "mapEntityToTariffType")
    @Mapping(target = "rate", source = "entity", qualifiedByName = "mapEntityToRate")
    SavingsAccountResponseDto toResponseDto(SavingsAccount entity);

    @Named("mapEntityToTariffType")
    default TariffType mapEntityToTariffType(SavingsAccount entity) {
        return entity.getCurrentTariff().getTariffType();
    }

    @Named("mapEntityToRate")
    default Double mapEntityToRate(SavingsAccount entity) {
        return entity.getCurrentTariff().getCurrentRate();
    }
}
