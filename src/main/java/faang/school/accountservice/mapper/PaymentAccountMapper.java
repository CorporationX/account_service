package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.paymentAccount.CreatePaymentAccountDto;
import faang.school.accountservice.dto.paymentAccount.PaymentAccountDto;
import faang.school.accountservice.model.PaymentAccount;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PaymentAccountMapper {
    PaymentAccount toPaymentAccount(PaymentAccountDto paymentAccountDto);

    PaymentAccountDto toPaymentAccountDto(PaymentAccount paymentAccount);

    PaymentAccount createPaymentAccountDtoToPaymentAccount(CreatePaymentAccountDto createPaymentAccountDto);
}
