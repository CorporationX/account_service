package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.balance.BalanceDto;
import faang.school.accountservice.dto.balance.CreateBalanceDto;
import faang.school.accountservice.dto.balance.UpdateBalanceDto;
import faang.school.accountservice.model.Balance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface BalanceMapper {

    Balance toBalance(CreateBalanceDto createBalanceDto);

    @Mapping(
            target = "authorizationBalance",
            defaultValue = "0L"
    )
    @Mapping(
            target = "actualBalance",
            defaultValue = "0L"
    )
    Balance toBalance(UpdateBalanceDto updateBalanceDto);

    @Mapping(target = "accountId", source = "account.id")
    BalanceDto toBalanceDto(Balance balance);

    void update(UpdateBalanceDto updateBalanceDto, @MappingTarget Balance balance);
}
