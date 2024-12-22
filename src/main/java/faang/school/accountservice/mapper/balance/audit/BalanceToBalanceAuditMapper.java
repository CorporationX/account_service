package faang.school.accountservice.mapper.balance.audit;

import faang.school.accountservice.dto.balance.BalanceDto;
import faang.school.accountservice.model.balance.audit.BalanceAudit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BalanceToBalanceAuditMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "authorizationAmount", source = "authorizedValue")
    @Mapping(target = "actualAmount", source = "actualValue")
    BalanceAudit balabceToBalanceAudit(BalanceDto balanceDto);

}
