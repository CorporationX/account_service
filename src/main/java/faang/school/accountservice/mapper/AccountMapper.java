package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.dto.AccountPreviewDto;
import faang.school.accountservice.dto.CreateAccountDto;
import faang.school.accountservice.model.Account;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring", uses = {AccountBalanceMapper.class})
public interface AccountMapper {
    Account toEntity(CreateAccountDto createAccountDto);

    AccountDto toDto(Account account);

    @Named("toPreviewDto")
    AccountPreviewDto toPreviewDto(Account account);

    @IterableMapping(qualifiedByName = "toPreviewDto")
    List<AccountPreviewDto> toPreviewDto(List<Account> accounts);
}
