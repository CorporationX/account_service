package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.dto.account.CreateAccountDto;
import faang.school.accountservice.entity.account.Account;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
//        uses = { UserMapper.class },
public interface AccountMapper {
    Account toAccount(CreateAccountDto createAccountDto);

    AccountDto toAccountDto(Account account);
}
