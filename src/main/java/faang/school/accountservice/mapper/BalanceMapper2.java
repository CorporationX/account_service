package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.balance.BalanceViewDto;
import faang.school.accountservice.model.Balance2;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BalanceMapper2 {
    @Mapping(source = "account.id", target = "accountId")
    BalanceViewDto toViewDto(Balance2 balance2);
}
