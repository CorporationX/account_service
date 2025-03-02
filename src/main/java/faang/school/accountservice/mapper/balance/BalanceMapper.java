package faang.school.accountservice.mapper.balance;

import faang.school.accountservice.dto.balance.BalanceDto;
import faang.school.accountservice.entity.balance.Balance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BalanceMapper {

    @Mapping(target = "account", ignore = true)
    Balance toEntity(BalanceDto dto);

    @Mapping(source = "balance.account.id", target = "accountId")
    BalanceDto toDto(Balance balance);

    Balance update(BalanceDto dto, @MappingTarget Balance balance);
}