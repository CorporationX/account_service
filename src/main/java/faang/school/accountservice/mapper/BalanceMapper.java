package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.Balance;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class BalanceMapper {

    public abstract Balance toEntity(BalanceDto dto, @Context Account account);

    @Mapping(target = "accountId", source = "account.id")
    public abstract BalanceDto toDto(Balance balance, @Context Account account);
}
