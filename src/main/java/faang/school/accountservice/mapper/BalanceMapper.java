package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.balance.BalanceDto;
import faang.school.accountservice.dto.balance.UpdateBalanceRequest;
import faang.school.accountservice.entity.balance.Balance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BalanceMapper {
    Balance toBalance(UpdateBalanceRequest updateBalanceRequest);

    @Mapping(target = "accountId", ignore = true)
    BalanceDto toReturnedBalanceDto(Balance balance);
}
