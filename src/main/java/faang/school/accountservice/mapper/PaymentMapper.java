package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.payment.PaymentDto;
import faang.school.accountservice.dto.payment.PaymentDtoToCreate;
import faang.school.accountservice.model.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PaymentMapper {

    Payment toEntity(PaymentDtoToCreate dto);

    PaymentDto toDto(Payment entity);
}