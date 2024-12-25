package faang.school.accountservice.mapper.account;

import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.entity.account.Account;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountMapper {

    AccountDto toDto(Account account);
    Account toEntity(AccountDto accountDto);
    List<AccountDto> toDto(List<Account> accounts);

}
