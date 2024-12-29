package faang.school.accountservice.mapper.balance;

import faang.school.accountservice.dto.balance.ResponseBalanceDto;
import faang.school.accountservice.model.balance.Balance;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface BalanceMapper {
    ResponseBalanceDto toDto(Balance balance);
}
