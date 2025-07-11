package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.balance.BalanceAuditDto;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.entity.BalanceAudit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BalanceAuditMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "accountVersion", ignore = true)
    BalanceAudit toEntity(BalanceAuditDto balanceAuditDto);

    BalanceAuditDto toDto(BalanceAudit balanceAudit);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "account", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    Balance toBalance(BalanceAudit audit);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "operationId", ignore = true)
    @Mapping(target = "accountNumber", source = "account.number")
    @Mapping(target = "accountVersion", source = "account.version")
    BalanceAudit toBalanceAudit(Balance balance);
}
