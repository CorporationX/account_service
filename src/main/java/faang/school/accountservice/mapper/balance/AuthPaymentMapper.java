package faang.school.accountservice.mapper.balance;

import faang.school.accountservice.dto.balance.response.AuthPaymentResponseDto;
import faang.school.accountservice.model.balance.AuthPayment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AuthPaymentMapper {
    @Mapping(source = "balance.id", target = "balanceId")
    AuthPaymentResponseDto toAuthPaymentResponseDto(AuthPayment authPayment);
}
