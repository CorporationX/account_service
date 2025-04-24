package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.AccountResponse;
import faang.school.accountservice.entity.Account;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    AccountResponse toDto(Account account);
}
