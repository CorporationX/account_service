package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.AccountBalanceDto;
import faang.school.accountservice.model.AccountBalance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccountBalanceMapper {
    AccountBalance toEntity(AccountBalanceDto accountBalanceDto);

    @Mapping(source = "account.accountNumber", target = "accountNumber")
    AccountBalanceDto toDto(AccountBalance accountBalance);
}
