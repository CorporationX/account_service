package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.balance.BalanceDto;
import faang.school.accountservice.model.balance.Balance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BalanceMapper {

    @Mapping(target = "authorizationBalance", source = "authorization")
    @Mapping(target = "actualBalance", source = "actual")
    BalanceDto toDto(Balance balance);

    @Mapping(target = "authorization", source = "authorizationBalance")
    @Mapping(target = "actual", source = "actualBalance")
    Balance toEntity(BalanceDto balanceDto);
}
