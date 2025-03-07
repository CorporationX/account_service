package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.BalanceResponseDto;
import faang.school.accountservice.entity.Balance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BalanceMapper {

    @Mapping(target = "accountId", source = "account.id")
    BalanceResponseDto toBalanceResponseDto(Balance balance);
}
