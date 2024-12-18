package faang.school.accountservice.mappers;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.model.Balance;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BalanceMapper {
    BalanceDto toDto(Balance balance);
    Balance toEntity(BalanceDto balanceDto);
}