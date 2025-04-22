package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.AccountResponseDto;
import faang.school.accountservice.dto.AccountTypeDto;
import faang.school.accountservice.dto.CurrencyDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    @Mapping(source = "currency", target = "currency")
    @Mapping(source = "accountType", target = "accountType")
    AccountResponseDto toAccountDto(Account account);

    default CurrencyDto mapCurrencyToCurrencyDto(Currency currency) {
        if (currency == null) {
            return null;
        }
        return CurrencyDto.valueOf(currency.name());
    }

    default AccountTypeDto mapCurrencyToCurrencyDto(AccountType accountType) {
        if (accountType == null) {
            return null;
        }
        return AccountTypeDto.valueOf(accountType.name());
    }
}
