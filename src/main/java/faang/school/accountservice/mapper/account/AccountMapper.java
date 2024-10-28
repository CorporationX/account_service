package faang.school.accountservice.mapper.account;

import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.mapper.owner.OwnerMapper;
import faang.school.accountservice.mapper.type.TypeMapper;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {OwnerMapper.class, TypeMapper.class})
public interface AccountMapper {

    AccountDto toAccountDto(Account account);

    List<AccountDto> toAccountDtos(List<Account> accounts);
}
