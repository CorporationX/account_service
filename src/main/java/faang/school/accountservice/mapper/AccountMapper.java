package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountMapper {
    @Mapping(target = "type", expression = "java(account.getType() != null ? account.getType().name() : null)")
    @Mapping(target = "currency", expression = "java(account.getCurrency() != null ? account.getCurrency().name() : null)")
    @Mapping(target = "status", expression = "java(account.getStatus() != null ? account.getStatus().name() : null)")
    AccountDto toDto(Account account);

    @Mapping(target = "type", expression = "java(mapType(accountDto.type()))")
    @Mapping(target = "currency", expression = "java(mapCurrency(accountDto.currency()))")
    @Mapping(target = "status", expression = "java(mapStatus(accountDto.status()))")
    Account toEntity(AccountDto accountDto);

    default AccountType mapType(String type) {
        return type != null ? AccountType.valueOf(type.toUpperCase()) : null;
    }

    default Currency mapCurrency(String currency) {
        return currency != null ? Currency.valueOf(currency.toUpperCase()) : null;
    }

    default AccountStatus mapStatus(String status) {
        return status != null ? AccountStatus.valueOf(status.toUpperCase()) : null;
    }
}
