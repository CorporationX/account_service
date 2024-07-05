package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.balance.BalanceUpdateDto;
import faang.school.accountservice.dto.balance_audit.BalanceAuditDto;
import faang.school.accountservice.model.BalanceAudit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BalanceAuditMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "accountId", target = "account.id")
    @Mapping(source = "authorizedBalance", target = "authorizationBalance")
    BalanceAudit toAudit(BalanceUpdateDto balanceUpdateDto);

    @Mapping(source = "account.id", target = "accountId")
    BalanceAuditDto toDto(BalanceAudit balanceAudit);
}
