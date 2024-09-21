package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.CashbackTariffDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.CashbackMerchantMapping;
import faang.school.accountservice.entity.CashbackOperationMapping;
import faang.school.accountservice.entity.CashbackTariff;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface CashbackTariffMapper {
    CashbackTariffDto toDto(CashbackTariff cashbackTariff);

    @Mapping(target = "accounts", ignore = true)
    @Mapping(target = "operationMappings", ignore = true)
    @Mapping(target = "merchantMappings", ignore = true)
    CashbackTariff toEntity(CashbackTariffDto cashbackTariffDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "accounts", ignore = true)
    @Mapping(target = "operationMappings", ignore = true)
    @Mapping(target = "merchantMappings", ignore = true)
    void updateEntityFromDto(CashbackTariffDto cashbackTariffDto, @MappingTarget CashbackTariff cashbackTariff);

    default Set<Long> mapAccountsToIds(Set<Account> accounts) {
        return accounts != null ? accounts.stream()
            .map(Account::getId)
            .collect(Collectors.toSet()) : null;
    }

    default Set<Long> mapOperationMappingsToIds(Set<CashbackOperationMapping> operationMappings) {
        return operationMappings != null ? operationMappings.stream()
            .map(CashbackOperationMapping::getId)
            .collect(Collectors.toSet()) : null;
    }

    default Set<Long> mapMerchantMappingsToIds(Set<CashbackMerchantMapping> merchantMappings) {
        return merchantMappings != null ? merchantMappings.stream()
            .map(CashbackMerchantMapping::getId)
            .collect(Collectors.toSet()) : null;
    }
}

