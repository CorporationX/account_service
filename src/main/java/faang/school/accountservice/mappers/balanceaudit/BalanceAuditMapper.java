package faang.school.accountservice.mappers.balanceaudit;

import faang.school.accountservice.dto.balanceaudit.BalanceAuditDto;
import faang.school.accountservice.model.Balance;
import faang.school.accountservice.model.BalanceAudit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BalanceAuditMapper {

    @Mapping(target = "accountId", source = "account.id")
    BalanceAuditDto toDto(BalanceAudit balanceAudit);

    BalanceAudit toEntity(BalanceAuditDto balanceAuditDto);

    @Mapping(target = "account", source = "balance.account")
    @Mapping(source = "balance.version", target = "versionId")
    @Mapping(source = "balance.authBalance", target = "authorizationBalance")
    @Mapping(source = "balance.actualBalance", target = "currentBalance")
    BalanceAudit toBalanceAudit(Balance balance);
}
