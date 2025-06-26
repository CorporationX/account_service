package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.model.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {BalanceMapper.class})
public interface AccountMapper {
    @Mapping(source = "balance.id", target = "balanceId")
    AccountDto toDto(Account account);

    Account toEntity(AccountDto accountDto);

    @Mapping(target = "accountNumber", ignore = true)
    void update(@MappingTarget Account account, AccountDto accountDto);
}