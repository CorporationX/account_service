package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.AccountResponse;
import faang.school.accountservice.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    @Mapping(target = "balance", source = "balance.actualBalance")
    AccountResponse toResponse(Account account);
}