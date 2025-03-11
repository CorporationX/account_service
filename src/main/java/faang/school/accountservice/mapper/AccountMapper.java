package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.AccountResponse;
import faang.school.accountservice.model.account.Account;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface AccountMapper {
    AccountResponse toDto(Account account);
}

