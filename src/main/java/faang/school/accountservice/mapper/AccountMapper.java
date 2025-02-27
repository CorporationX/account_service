package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.AccountCreateDto;
import faang.school.accountservice.dto.AccountResponseDto;
import faang.school.accountservice.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountMapper {
    Account toEntity(AccountCreateDto accountCreateDto);

    AccountCreateDto toAccountCreateDto(Account account);

    Account toEntity(AccountResponseDto accountResponseDto);

    AccountResponseDto toDto(Account account);

    List<AccountResponseDto> toDtoList(List<Account> accounts);
}