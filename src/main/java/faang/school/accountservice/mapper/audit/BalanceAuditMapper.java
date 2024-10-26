package faang.school.accountservice.mapper.audit;

import faang.school.accountservice.model.dto.audit.BalanceAuditDto;
import faang.school.accountservice.model.entity.BalanceAudit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BalanceAuditMapper {

    @Mapping(source = "balance.id", target = "balanceId")
    BalanceAuditDto toDto(BalanceAudit balanceAudit);

    List<BalanceAuditDto> toDto(List<BalanceAudit> balanceAuditList);
}
