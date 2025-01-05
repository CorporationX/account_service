package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.account.AccountReq;
import faang.school.accountservice.dto.account.AccountResp;
import faang.school.accountservice.model.Account;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    AccountResp accountToAccountResp(Account account);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Account accountReqToAccount(AccountReq accountReq);
}
