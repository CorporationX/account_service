package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.PaymentEventDto;
import faang.school.accountservice.entity.PaymentOperation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PaymentOperationMapper {
    @Mapping(target = "debitAccount", source = "debitAccountId", ignore = true)
    @Mapping(target = "creditAccount", source = "creditAccountId", ignore = true)
    @Mapping(target = "id", source = "paymentNumber")
    PaymentOperation toModel(PaymentEventDto paymentEventDto);
}
