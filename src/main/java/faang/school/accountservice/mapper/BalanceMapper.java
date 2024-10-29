package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.balance.BalanceDtoWhenUpdate;
import faang.school.accountservice.dto.balance.ReturnedBalanceDto;
import faang.school.accountservice.entity.balance.Balance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BalanceMapper {

    Balance toBalance(BalanceDtoWhenUpdate balanceDtoWhenUpdate);

    @Mapping(target = "accountId", ignore = true)
    ReturnedBalanceDto toReturnedBalanceDto(Balance balance);
}
