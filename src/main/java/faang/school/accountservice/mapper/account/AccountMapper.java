package faang.school.accountservice.mapper.account;

import faang.school.accountservice.dto.account.AccountCreateProjectRequestDto;
import faang.school.accountservice.dto.account.AccountCreateUserRequestDto;
import faang.school.accountservice.dto.account.AccountResponseDto;
import faang.school.accountservice.entity.account.Account;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        builder = @Builder(disableBuilder = true))
public interface AccountMapper {
    @Mapping(source = "currency.id", target = "currencyId")
    AccountResponseDto toAccountResponseDto(Account account);

    Account toAccountEntity(AccountCreateProjectRequestDto accountCreateProjectRequestDto);

    Account toAccountEntity(AccountCreateUserRequestDto accountCreateUserRequestDto);
}
