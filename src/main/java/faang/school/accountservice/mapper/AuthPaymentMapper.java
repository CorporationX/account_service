package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.auth_payment.authorize.AuthorizationMessageRequest;
import faang.school.accountservice.entity.AuthPayment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AuthPaymentMapper {
    @Mapping(target = "receiverAccount", ignore = true)
    @Mapping(target = "senderAccount", ignore = true)
    AuthPayment toEntity(AuthorizationMessageRequest authorizationMessageRequest);
}