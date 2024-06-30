package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.entity.Balance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BalanceMapper {

    Balance toEntity(BalanceDto balanceDto);

    @Mapping(source = "account.id", target = "accountId")
    @Mapping(source = "authorization_Balance", target = "authorizationBalance")
    @Mapping(source = "actual_Balance", target = "actualBalance")
    BalanceDto toDto(Balance balance);
}
