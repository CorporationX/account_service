package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.Balance;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface BalanceMapper {

    @Mapping(source = "account.id", target = "accountId")
    BalanceDto toDto(Balance balance);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "account", source="account")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    Balance toEntity(BalanceDto balanceDto, Account account);

}
