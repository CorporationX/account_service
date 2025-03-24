package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.BalanceAuditResponseDto;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.entity.BalanceAudit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BalanceAuditMapper {
    @Mapping(target = "balanceId", source = "id")
    BalanceAuditResponseDto toBalanceAuditResponseDto(BalanceAudit balanceAudit);

    @Mapping(target = "operationId", expression = "java(123555)")
    @Mapping(target = "balanceId", source = "id")
    BalanceAudit toBalanceAuditFromBalance(Balance balance);

    @Mapping(target = "operationId", expression = "java(123456)")
    BalanceAuditResponseDto toBalanceAuditResponseDtoFromBalance(Balance balance);
}
