package faang.school.accountservice.mappers;

import faang.school.accountservice.dto.cashbackdto.CashbackTariffDto;
import faang.school.accountservice.model.CashbackMerchantMapping;
import faang.school.accountservice.model.CashbackOperationMapping;
import faang.school.accountservice.model.CashbackTariff;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CashbackTariffMapper {

    @Mapping(source = "operationMappings", target = "operationMappings")
    @Mapping(source = "merchantMappings", target = "merchantMappings")
    CashbackTariffDto toDto(CashbackTariff tariff,
                            List<CashbackOperationMapping> operationMappings,
                            List<CashbackMerchantMapping> merchantMappings);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    CashbackTariff toEntity(CashbackTariffDto dto);
}