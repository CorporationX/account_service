package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.dto.SavingsAccountDto;
import faang.school.accountservice.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountMapper {

    AccountDto toDto(Account account);

    Account toEntity(AccountDto accountDto);

    @Mapping(target = "id", source = "accountId")
    @Mapping(target = "holderUserId", source = "holderUserId")
    Account toEntity(SavingsAccountDto dto);
}
