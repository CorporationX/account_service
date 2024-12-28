package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.balance.BalanceAuditDto;
import faang.school.accountservice.model.balance.Balance;
import faang.school.accountservice.model.balance.BalanceAudit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BalanceAuditMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "balanceVersion", source = "version")
    @Mapping(target = "accountId", source = "account.id")
    BalanceAudit toAuditEntity(Balance balance);

    BalanceAuditDto toDto(BalanceAudit balanceAudit);

    List<BalanceAuditDto> toListAuditDto(List<BalanceAudit> balanceAudits);
}
