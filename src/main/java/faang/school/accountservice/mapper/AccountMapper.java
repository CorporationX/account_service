package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.account.AccountCreateDto;
import faang.school.accountservice.model.Account;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountMapper {
    Account toEntity(AccountCreateDto dto);
}
