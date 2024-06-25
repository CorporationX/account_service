package faang.school.accountservice.mapper;


import faang.school.accountservice.dto.BalanceAuditDto;
import faang.school.accountservice.model.Balance;
import faang.school.accountservice.model.BalanceAudit;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BalanceAuditMapper {

    BalanceAudit toAuditEntity(Balance balance);

    BalanceAuditDto toDto(BalanceAudit balanceAudit);

    List<BalanceAuditDto> toListAuditDto(List<BalanceAudit> balanceAudits);
}