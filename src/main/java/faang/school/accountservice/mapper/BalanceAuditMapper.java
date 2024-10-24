package faang.school.accountservice.mapper;

import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.entity.BalanceAudit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BalanceAuditMapper {
    @Mapping(target = "number", source = "account.number")
    @Mapping(target = "version", source = "version")
    @Mapping(target = "curAuthBalance", source = "curAuthBalance")
    @Mapping(target = "curFactBalance", source = "curFactBalance")
    BalanceAudit toEntity(Balance balance);
}
