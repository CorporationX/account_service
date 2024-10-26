package faang.school.accountservice.mapper;

import faang.school.accountservice.model.dto.AccountDto;
import faang.school.accountservice.model.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountMapper {

    Account toEntity(AccountDto accountDto);

    AccountDto toDto(Account account);

    List<AccountDto> toDtoList(List<Account> accountList);
}
