package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.Balance;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountMapper {

//    @Mapping(source = "owner.id", target = "owner.owner_id")
    AccountDto toDto (Account account);

//    @Mapping(source = "owner.owner_id", target = "owner.id")
    Account toEntity (AccountDto accountDto);
}