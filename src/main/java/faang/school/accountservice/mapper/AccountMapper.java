package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.dto.account.OpenAccountDto;
import faang.school.accountservice.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountMapper {
    Account toEntity(AccountDto dto);

    Account toEntity(OpenAccountDto dto);

    AccountDto toDto(Account account);
}
