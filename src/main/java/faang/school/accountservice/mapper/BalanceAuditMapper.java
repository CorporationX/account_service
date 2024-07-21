package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.BalanceAuditDto;
import faang.school.accountservice.model.Balance;
import faang.school.accountservice.model.BalanceAudit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface BalanceAuditMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "accountId", source = "account.id")
    @Mapping(target = "versionBalance", source = "version")
    BalanceAudit toBalanceAudit(Balance balance);

    BalanceAuditDto toDto(BalanceAudit balanceAudit);


    List<BalanceAuditDto> toListDto(List<BalanceAudit> balanceAuditList);
}
