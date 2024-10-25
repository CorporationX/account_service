package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.account.AccountResponseDto;
import faang.school.accountservice.dto.account.OpenAccountDto;
import faang.school.accountservice.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountMapper {
    Account toEntity(OpenAccountDto dto);

    AccountResponseDto toResponseDto(Account account);
}
