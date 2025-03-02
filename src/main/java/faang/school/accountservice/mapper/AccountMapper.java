package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.account.Account;
import faang.school.accountservice.dto.account.AccountCreateDto;
import faang.school.accountservice.dto.account.AccountDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountMapper {
    Account fromCreateDto(AccountCreateDto accountDto);

    AccountDto toDto(Account account);
}
