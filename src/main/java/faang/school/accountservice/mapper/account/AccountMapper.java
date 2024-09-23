package faang.school.accountservice.mapper.account;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.AccountOwner;
import faang.school.accountservice.entity.Balance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountMapper {

    @Mapping(source = "balance", target = "balanceId", qualifiedByName = "balanceToId")
    @Mapping(source = "accountOwner", target = "accountOwnerId", qualifiedByName = "ownerToId")
    @Mapping(source = "accountOwner", target = "ownerType", qualifiedByName = "ownerToType")
    AccountDto toDto(Account account);

    @Named("balanceToId")
    default long balanceToId(Balance balance) {
        return balance.getId();
    }

    @Named("ownerToId")
    default long ownerToId(AccountOwner owner) {
        return owner.getId();
    }

    @Named("ownerToType")
    default String ownerToType(AccountOwner owner) {
        return owner.getOwnerType().name();
    }
}