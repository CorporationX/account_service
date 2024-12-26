package faang.school.accountservice.mapper.balance;

import faang.school.accountservice.dto.balance.BalanceDto;
import faang.school.accountservice.entity.balance.Balance;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BalanceMapper {
    BalanceDto toDto(Balance balance);

    Balance toEntity(BalanceDto balanceDto);
}