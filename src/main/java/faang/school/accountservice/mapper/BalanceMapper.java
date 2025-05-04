package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.BalanceResponseDto;
import faang.school.accountservice.dto.BalanceRequestDto;
import faang.school.accountservice.entity.Balance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BalanceMapper {
    @Mapping(target = "accountNumber" , source = "account.accountNumber")
    BalanceResponseDto toBalanceResponseDto(Balance balance);

    @Mapping(target = "account", ignore = true )
    Balance toBalance(BalanceResponseDto balanceResponseDto);

    @Mapping(target = "account", ignore = true)
    Balance toBalance(BalanceRequestDto balanceRequestDto);
}
