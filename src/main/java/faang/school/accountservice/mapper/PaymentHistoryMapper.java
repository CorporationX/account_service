package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.event.PaymentEvent;
import faang.school.accountservice.model.PaymentHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PaymentHistoryMapper {
    @Mapping(source = "paymentId", target = "externalPaymentKey")
    PaymentHistory toPaymentHistory(PaymentEvent paymentEvent);
}