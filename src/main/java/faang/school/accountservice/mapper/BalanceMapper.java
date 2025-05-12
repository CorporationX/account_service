package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.balance.ResponseBalanceDto;
import faang.school.accountservice.entity.Balance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BalanceMapper {

    @Mapping(target = "accountId", source = "balance.account.id")
    ResponseBalanceDto toDto(Balance balance);
}
