package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.model.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountMapper {
    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(source = "balance.currentBalance", target = "balance")
    AccountDto toDto(Account account);

    @Mapping(source = "ownerId", target = "owner.id")
    @Mapping(source = "balance", target = "balance.currentBalance")
    Account toEntity(AccountDto accountDto);
}