package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.AccountRequestDto;
import faang.school.accountservice.dto.AccountResponseDto;
import faang.school.accountservice.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountMapper {

    AccountResponseDto toAccountResponseDto(Account account);

    @Mapping(target = "balance", constant = "0.00")
    @Mapping(target = "accountStatus", constant = "ACTIVE")
    Account toAccountEntity(AccountRequestDto accountRequestDto);
}
