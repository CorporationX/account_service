package faang.school.accountservice.mapper.balance;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.entity.Balance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BalanceMapper {

    @Mapping(source = "account.id", target = "accountId")
    @Mapping(target = "actualAmount", expression = "java(defaultRound(balance.getActualAmount()))")
    @Mapping(target = "authorizedAmount", expression = "java(defaultRound(balance.getAuthorizedAmount()))")
    @Mapping(source = "account.currency", target = "currency")
    BalanceDto toDto(Balance balance);

    default BigDecimal defaultRound(BigDecimal bigDecimal) {
        return bigDecimal.setScale(2, RoundingMode.DOWN);
    }
}