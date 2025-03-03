package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.account.AccountCreateDto;
import faang.school.accountservice.dto.account.AccountReadDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.AccountStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        imports = AccountStatus.class
)
public interface AccountMapper {
    AccountReadDto toDto(Account account);

    @Mapping(target = "status", expression = "java(AccountStatus.OPEN)")
    Account toEntity(AccountCreateDto dto);
}
