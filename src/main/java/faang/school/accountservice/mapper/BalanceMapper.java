package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.balance.BalanceViewDto;
import faang.school.accountservice.model.Balance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BalanceMapper {
    @Mapping(source = "account.id", target = "accountId")
    BalanceViewDto toViewDto(Balance balance);
}
