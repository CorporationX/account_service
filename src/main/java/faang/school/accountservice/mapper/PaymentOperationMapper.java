package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.PaymentOperationDto;
import faang.school.accountservice.dto.PaymentResponseDto;
import faang.school.accountservice.entity.PaymentOperation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PaymentOperationMapper {
    @Mapping(target = "senderAccount", source = "senderAccountNumber", ignore = true)
    @Mapping(target = "receiverAccount", source = "receiverAccountNumber", ignore = true)
    @Mapping(target = "id", source = "paymentId")
    PaymentOperation toModel(PaymentOperationDto paymentOperationDto);

    PaymentResponseDto toPaymentResponseDto(PaymentOperationDto paymentOperationDto);
}
