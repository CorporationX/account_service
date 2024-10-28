package faang.school.accountservice.mapper;

import faang.school.accountservice.model.dto.BalanceDto;
import faang.school.accountservice.model.entity.Balance;
import faang.school.accountservice.model.entity.BalanceAudit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import java.util.UUID;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BalanceMapper {
    @Mapping(target = "accountId", source = "account.id")
    BalanceDto toDto(Balance balance);
    Balance toEntity(BalanceDto balanceDto);
    BalanceAudit toBalanceAudit(Balance balance, UUID operationId);
}
