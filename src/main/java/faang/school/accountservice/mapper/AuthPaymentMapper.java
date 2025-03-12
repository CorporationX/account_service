package faang.school.accountservice.mapper;

        import faang.school.accountservice.dto.balance.AuthPaymentResponseDto;
        import faang.school.accountservice.entity.AuthPayment;
        import org.mapstruct.Mapper;
        import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AuthPaymentMapper {

    AuthPaymentResponseDto toDto(AuthPayment payment);

    AuthPayment toEntity(AuthPaymentResponseDto authPaymentResponseDto);
}
