package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.balance.BalanceDto;
import faang.school.accountservice.entity.Balance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BalanceMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "authBalance", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "account.id", source = "accountId")
    Balance toEntity(BalanceDto balanceDto);

    @Mapping(target = "accountId", source = "account.id")
    BalanceDto toDto(Balance balance);
}
