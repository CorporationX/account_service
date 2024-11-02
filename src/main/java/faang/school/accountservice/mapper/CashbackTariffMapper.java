package faang.school.accountservice.mapper;

import faang.school.accountservice.model.cashback.CreateCashbackTariffDto;
import faang.school.accountservice.model.cashback.MerchantDto;
import faang.school.accountservice.model.cashback.OperationDto;
import faang.school.accountservice.model.cashback.ReadCashbackTariffDto;
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

    @Mapping(target = "operations",
            expression = "java(addOperations(cashbackTariff.getOperationTypes()))")
    @Mapping(target = "merchants",
            expression = "java(addMerchants(cashbackTariff.getCashbackTariffMerchants()))")
    ReadCashbackTariffDto toDto(CashbackTariff cashbackTariff);

    CashbackTariff toEntity(CreateCashbackTariffDto readCashbackTariffDto);

    default List<OperationDto> addOperations(List<CashbackTariffOperationType> operations) {
        var operationDtoList = new ArrayList<OperationDto>();
        operations.forEach(operation -> {
            var dto = OperationDto.builder()
                    .id(operation.getOperationType().getId())
                    .operationType(operation.getOperationType().getOperationType())
                    .percentage(operation.getPercentage())
                    .build();
            operationDtoList.add(dto);
        });
        return operationDtoList;
    }

    default List<MerchantDto> addMerchants(List<CashbackTariffMerchant> merchants) {
        var merchantDtoList = new ArrayList<MerchantDto>();
        merchants.forEach(merchant -> {
            var dto = MerchantDto.builder()
                    .id(merchant.getId())
                    .merchantId(merchant.getMerchant().getMerchantId())
                    .percentage(merchant.getPercentage())
                    .build();

            merchantDtoList.add(dto);
        });
        return merchantDtoList;
    }
}
