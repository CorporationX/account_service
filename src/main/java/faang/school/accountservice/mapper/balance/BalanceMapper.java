package faang.school.accountservice.mapper.balance;

import faang.school.accountservice.dto.balance.BalanceResponseDto;
import faang.school.accountservice.entity.balance.Balance;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        builder = @Builder(disableBuilder = true))
public interface BalanceMapper {
    @Mapping(source = "account.id", target = "accountId")
    BalanceResponseDto toBalanceResponseDto(Balance balance);
}
