package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.AccountBalanceDto;
import faang.school.accountservice.model.AccountBalance;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface AccountBalanceMapper {
    AccountBalance toEntity(AccountBalanceDto accountBalanceDto);

    AccountBalanceDto toDto(AccountBalance accountBalance);
}
