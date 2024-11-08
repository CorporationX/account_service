package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.cashback.CashbackMerchantDto;
import faang.school.accountservice.dto.cashback.CashbackOperationTypeDto;
import faang.school.accountservice.dto.cashback.CashbackTariffResponseDto;
import faang.school.accountservice.dto.cashback.CashbackTariffShortResponseDto;
import faang.school.accountservice.dto.cashback.CreateCashbackMerchantDto;
import faang.school.accountservice.dto.cashback.CreateCashbackOperationTypeDto;
import faang.school.accountservice.dto.cashback.UpdateCashbackMerchantDto;
import faang.school.accountservice.dto.cashback.UpdateCashbackOperationTypeDto;
import faang.school.accountservice.entity.cacheback.CashbackMerchant;
import faang.school.accountservice.entity.cacheback.CashbackOperationType;
import faang.school.accountservice.entity.cacheback.CashbackTariff;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CashbackMapper {
    @Mapping(target = "cashbackOperationTypes", source = "cashbackOperationTypes", qualifiedByName = "mapTypes")
    @Mapping(target = "cashbackMerchants", source = "cashbackMerchants", qualifiedByName = "mapMerchants")
    CashbackTariffResponseDto toResponseDto(CashbackTariff cashbackTariff);

    CashbackTariffShortResponseDto toShortResponseDto(CashbackTariff cashbackTariff);

    @Mapping(target = "cashbackTariffId", source = "cashbackTariff.id")
    CashbackOperationTypeDto toCashbackOperationTypeDto(CashbackOperationType cashbackOperationType);

    @Mapping(target = "cashbackTariffId", source = "cashbackTariff.id")
    @Mapping(target = "merchantId", source = "merchant.id")
    CashbackMerchantDto toCashbackMerchantDto(CashbackMerchant cashbackMerchant);

    @Mapping(target = "merchant.id", source = "merchantId")
    CashbackMerchant toCashbackMerchantEntity(CreateCashbackMerchantDto cashbackMerchant);

    CashbackMerchant toCashbackMerchantEntity(UpdateCashbackMerchantDto cashbackMerchant);

    CashbackOperationType toCashbackOperationType(CreateCashbackOperationTypeDto cashbackMerchant);

    CashbackOperationType toCashbackOperationType(UpdateCashbackOperationTypeDto cashbackMerchant);

    @Named("mapTypes")
    default List<CashbackOperationTypeDto> mapTypes(Set<CashbackOperationType> cashbackOperationTypes) {
        return cashbackOperationTypes.stream()
                .map(this::toCashbackOperationTypeDto)
                .toList();
    }

    @Named("mapMerchants")
    default List<CashbackMerchantDto> mapMerchants(Set<CashbackMerchant> cashbackMerchants) {
        return cashbackMerchants.stream()
                .map(this::toCashbackMerchantDto)
                .toList();
    }
}