package faang.school.accountservice.mapper.account;

import faang.school.accountservice.dto.account.BalanceDto;
import faang.school.accountservice.entity.account.Balance;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BalanceMapper {
    BalanceDto toDto(Balance balance);

    Balance toEntity(BalanceDto balanceDto);
}
