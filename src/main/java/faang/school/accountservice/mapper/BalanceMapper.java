package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.model.Balance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {AccountMapper.class})
public interface BalanceMapper {

    @Mapping(source = "accountId", target = "account.id")
    @Mapping(target = "version", ignore = true)
    Balance toEntity(BalanceDto balanceDto);

    @Mapping(source = "account.id", target = "accountId")
    BalanceDto toDto(Balance balance);

    @Mapping(source = "authorizedBalance", target = "authorizedBalance")
    @Mapping(source = "actualBalance", target = "actualBalance")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "currency", ignore = true)
    @Mapping(target = "version", ignore = true)
    void updateBalance(BalanceDto balanceDto, @MappingTarget Balance balance);
}
