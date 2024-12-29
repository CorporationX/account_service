package faang.school.accountservice.mapper.account;

import faang.school.accountservice.dto.account.RequestAccountDto;
import faang.school.accountservice.dto.account.ResponseAccountDto;
import faang.school.accountservice.model.account.Account;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE
)
public interface AccountMapper {
    Account toEntity(RequestAccountDto requestAccountDto);
    ResponseAccountDto toDto(Account account);
}
