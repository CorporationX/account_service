package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.balance.BalanceCreateDto;
import faang.school.accountservice.dto.balance.BalanceDto;
import faang.school.accountservice.model.balance.Balance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BalanceMapper {

    @Mapping(target = "accountId", source = "account.id")
    BalanceDto toDto(Balance balance);

    Balance toEntity(BalanceDto dto);

    @Mapping(target = "account", ignore = true)
    Balance toEntity(BalanceCreateDto dto);

}