package faang.school.accountservice.mapper.balance;

import faang.school.accountservice.dto.balance.response.BalanceResponseDto;
import faang.school.accountservice.model.balance.Balance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BalanceMapper {
    @Mapping(source = "account.accountNumber", target = "accountNumber")
    BalanceResponseDto toBalanceResponseDto(Balance balance);
}
