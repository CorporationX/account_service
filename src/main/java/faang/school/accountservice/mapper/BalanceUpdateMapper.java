package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.balance.UpdateBalanceDto;
import faang.school.accountservice.entity.balance.Balance;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface BalanceUpdateMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateBalanceFromDto(UpdateBalanceDto dto, @MappingTarget Balance balance);
}