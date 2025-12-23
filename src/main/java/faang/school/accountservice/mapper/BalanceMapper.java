package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.balance.ResponseBalanceDto;
import faang.school.accountservice.entity.balance.Balance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BalanceMapper {
    @Mapping(source = "id",
             target = "balanceId")
    @Mapping(source = "account.id",
             target = "accountId")
    ResponseBalanceDto toDto(Balance balance);
}