package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.ResponseBalanceDto;
import faang.school.accountservice.entity.Balance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BalanceMapper {

    @Mapping(target = "accountId", source = "account.id")
    ResponseBalanceDto toResponseBalanceDto(Balance balance);
}