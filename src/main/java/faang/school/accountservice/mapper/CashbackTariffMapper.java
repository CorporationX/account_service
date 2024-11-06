package faang.school.accountservice.mapper;

import faang.school.accountservice.model.cashback.CreateCashbackTariffDto;
import faang.school.accountservice.model.cashback.ReadCashbackTariffDto;
import faang.school.accountservice.model.cashback.ReadMerchantDto;
import faang.school.accountservice.model.cashback.ReadOperationDto;
import faang.school.accountservice.model.entity.cashback.CashbackTariff;
import faang.school.accountservice.model.entity.cashback.CashbackTariffMerchant;
import faang.school.accountservice.model.entity.cashback.CashbackTariffOperationType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CashbackTariffMapper {

    @Mapping(target = "operations", expression = "java(addOperationsToDto(cashbackTariff.getOperationTypes()))")
    @Mapping(target = "merchants", expression = "java(addMerchantsToDto(cashbackTariff.getCashbackTariffMerchants()))")
    ReadCashbackTariffDto toDto(CashbackTariff cashbackTariff);

    CashbackTariff toEntity(CreateCashbackTariffDto cashbackTariffDto);

    default List<ReadOperationDto> addOperationsToDto(List<CashbackTariffOperationType> operations) {
        var operationDtoList = new ArrayList<ReadOperationDto>();
        operations.forEach(operation -> {
            var dto = ReadOperationDto.builder()
                    .id(operation.getOperationType().getId())
                    .operationType(operation.getOperationType().getOperationType())
                    .percentage(operation.getPercentage())
                    .build();
            operationDtoList.add(dto);
        });
        return operationDtoList;
    }

    default List<ReadMerchantDto> addMerchantsToDto(List<CashbackTariffMerchant> merchants) {
        var merchantDtoList = new ArrayList<ReadMerchantDto>();
        merchants.forEach(merchant -> {
            var dto = ReadMerchantDto.builder()
                    .id(merchant.getMerchant().getId())
                    .merchantName(merchant.getMerchant().getName())
                    .percentage(merchant.getPercentage())
                    .build();
            merchantDtoList.add(dto);
        });
        return merchantDtoList;
    }
}
