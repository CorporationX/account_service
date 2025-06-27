package faang.school.accountservice.mapper;


import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.model.Balance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BalanceMapper {
    @Mapping(source = "account.id", target = "accountId")
    BalanceDto toDto(Balance balance);

    Balance toEntity(BalanceDto balanceDto);

    @Mapping(target = "accountNumber", ignore = true)
    void update(@MappingTarget Balance balance, BalanceDto dto);
}