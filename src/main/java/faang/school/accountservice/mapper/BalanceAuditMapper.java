package faang.school.accountservice.mapper;

import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.entity.BalanceAudit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BalanceAuditMapper {

    @Mapping(target = "operationId", source = "operationId")
    @Mapping(target = "account", source = "balance.account")
    @Mapping(target = "balanceVersion", source = "balance.balanceVersion")
    @Mapping(target = "authorizedBalance", source = "balance.authorizedBalance")
    @Mapping(target = "actualBalance", source = "balance.actualBalance")
    BalanceAudit toBalanceAudit(Balance balance, Long operationId);
}

