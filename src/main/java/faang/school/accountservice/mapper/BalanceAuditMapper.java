package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.BalanceAuditResponseDto;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.entity.BalanceAudit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BalanceAuditMapper {
    @Mapping(target = "balanceId", source = "balance.id")
    BalanceAuditResponseDto toBalanceAuditResponseDto(BalanceAudit balanceAudit);

    @Mapping(target = "operationId", expression = "java(123456)")
    BalanceAudit toBalanceAuditFromBalance(Balance balance);
}
