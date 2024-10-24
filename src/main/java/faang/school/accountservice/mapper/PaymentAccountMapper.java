package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.PaymentAccountDto;
import faang.school.accountservice.entity.PaymentAccount;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PaymentAccountMapper {
    PaymentAccountDto toDto(PaymentAccount paymentAccount);

    PaymentAccount toEntity(PaymentAccountDto paymentAccountDto);
}