package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.AccountRequestDto;
import faang.school.accountservice.dto.AccountResponseDto;
import faang.school.accountservice.dto.AccountTypeDto;
import faang.school.accountservice.dto.CurrencyDto;
import faang.school.accountservice.dto.OwnerTypeDto;
import faang.school.accountservice.dto.StatusDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OwnerType;
import faang.school.accountservice.enums.Status;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    @Mapping(source = "currency", target = "currency", qualifiedByName = "mapCurrencyToCurrencyDto")
    @Mapping(source = "accountType", target = "accountType", qualifiedByName = "mapAccountTypeToAccountTypeDto")
    @Mapping(source = "ownerType", target = "ownerType", qualifiedByName = "mapOwnerTypeToOwnerTypeDto")
    @Mapping(source = "status", target = "status", qualifiedByName = "mapStatusToStatusDto")
    AccountResponseDto toAccountResponseDto(Account account);

    @Mapping(source = "currency", target = "currency", qualifiedByName = "mapCurrencyDtoToCurrency")
    @Mapping(source = "accountType", target = "accountType", qualifiedByName = "mapAccountTypeDtoToAccountType")
    @Mapping(source = "ownerType", target = "ownerType", qualifiedByName = "mapOwnerTypeDtoToOwnerType")
    Account toAccount(AccountRequestDto accountRequestDto);

    @Named("mapCurrencyToCurrencyDto")
    default CurrencyDto mapCurrencyToCurrencyDto(Currency currency) {
        return currency == null ? null : CurrencyDto.valueOf(currency.name());
    }

    @Named("mapCurrencyDtoToCurrency")
    default Currency mapCurrencyDtoToCurrency(CurrencyDto currency) {
        return currency == null ? null : Currency.valueOf(currency.name());
    }

    @Named("mapAccountTypeToAccountTypeDto")
    default AccountTypeDto mapAccountTypeToAccountTypeDto(AccountType accountType) {
        return accountType == null ? null : AccountTypeDto.valueOf(accountType.name());
    }

    @Named("mapAccountTypeDtoToAccountType")
    default AccountType mapAccountTypeDtoToAccountType(AccountTypeDto accountType) {
        return accountType == null ? null : AccountType.valueOf(accountType.name());
    }

    @Named("mapOwnerTypeToOwnerTypeDto")
    default OwnerTypeDto mapOwnerTypeToOwnerTypeDto(OwnerType ownerType) {
        return ownerType == null ? null : OwnerTypeDto.valueOf(ownerType.name());
    }

    @Named("mapOwnerTypeDtoToOwnerType")
    default OwnerType mapOwnerTypeDtoToOwnerType(OwnerTypeDto ownerType) {
        return ownerType == null ? null : OwnerType.valueOf(ownerType.name());
    }

    @Named("mapStatusToStatusDto")
    default StatusDto mapStatusToStatusDto(Status status) {
        return status == null ? null : StatusDto.valueOf(status.name());
    }
}

