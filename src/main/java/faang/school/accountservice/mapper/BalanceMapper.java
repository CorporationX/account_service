package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.Balance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BalanceMapper {
    @Mapping(target = "accountNumber", source = "account", qualifiedByName = "accountConvertAccountNumber")
    BalanceDto toDto(Balance balance);

    Balance toEntity(BalanceDto balanceDto);

    @Named("accountConvertAccountNumber")
    default String accountConvertAccountNumber(Account account) {
        return account.getNumber();
    }
}
