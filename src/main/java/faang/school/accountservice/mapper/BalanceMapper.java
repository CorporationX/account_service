package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.balance.BalanceResponseDto;
import faang.school.accountservice.entity.Balance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BalanceMapper {

    @Mapping(source = "account.accountNumber", target = "accountNumber")
    BalanceResponseDto toBalanceResponseDto(Balance balance);
}
