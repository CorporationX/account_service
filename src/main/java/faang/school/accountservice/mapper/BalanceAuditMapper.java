package faang.school.accountservice.mapper;

import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.entity.BalanceAudit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BalanceAuditMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "balance.account", target = "account")
    @Mapping(source = "balance.version", target = "balanceVersion")
    @Mapping(source = "balance.authorizedBalance", target = "authorizedBalance")
    @Mapping(source = "balance.actualBalance", target = "actualBalance")
    @Mapping(source = "operationId", target = "operationId")
    BalanceAudit toAudit(Balance balance, Long operationId);
}
