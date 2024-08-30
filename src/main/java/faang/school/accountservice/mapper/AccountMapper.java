package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OwnerType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountMapper {
    @Mapping(source = "ownerType", target = "ownerType", qualifiedByName = "mappedOwnerType")
    @Mapping(source = "accountType", target = "accountType", qualifiedByName = "mappedAccountType")
    @Mapping(source = "currency", target = "currency", qualifiedByName = "mappedCurrency")
    @Mapping(source = "status", target = "status", qualifiedByName = "mappedAccountStatus")
    AccountDto toDto(Account account);
    @Mapping(source = "ownerType", target = "ownerType", qualifiedByName = "mappedOwnerType")
    @Mapping(source = "accountType", target = "accountType", qualifiedByName = "mappedAccountType")
    @Mapping(source = "currency", target = "currency", qualifiedByName = "mappedCurrency")
    @Mapping(source = "status", target = "status", qualifiedByName = "mappedAccountStatus")
    Account toEntity(AccountDto accountDto);

    @Named("mappedOwnerType")
    default String mappedOwnerType(OwnerType ownerType) {
        return ownerType != null ? ownerType.name() : null;
    }

    @Named("mappedOwnerType")
    default OwnerType mappedOwnerType(String ownerType) {
        return OwnerType.valueOf(ownerType);
    }

    @Named("mappedAccountType")
    default String mappedAccountType(AccountType accountType) {
        return accountType != null ? accountType.name() : null;
    }

    @Named("mappedAccountType")
    default AccountType mappedAccountType(String accountType) {
        return AccountType.valueOf(accountType);
    }

    @Named("mappedCurrency")
    default String mappedCurrency(Currency currency) {
        return currency != null ? currency.name() : null;
    }

    @Named("mappedCurrency")
    default Currency mappedCurrency(String currency) {
        return Currency.valueOf(currency);
    }

    @Named("mappedAccountStatus")
    default String mappedAccountStatus(AccountStatus accountStatus) {
        return accountStatus != null ? accountStatus.name() : null;
    }

    @Named("mappedAccountStatus")
    default AccountStatus mappedAccountStatus(String accountStatus) {
        return AccountStatus.valueOf(accountStatus);
    }
}
