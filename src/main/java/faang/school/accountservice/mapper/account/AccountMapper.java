package faang.school.accountservice.mapper.account;

import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.model.account.Account;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountMapper {
    AccountDto toDto(Account account);

    Account toEntity(AccountDto accountDto);
}