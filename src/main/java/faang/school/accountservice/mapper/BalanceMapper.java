package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.balance.BalanceViewDto;
import faang.school.accountservice.model.Balance;
import faang.school.accountservice.model.BalanceAudit;
import faang.school.accountservice.model.BalanceDMS;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface BalanceMapper {
    @Mapping(source = "account.id", target = "accountId")
    BalanceViewDto toViewDto(Balance balance);

    @Mapping(target = "paymentOperationId", source = "operationId")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "clearBalanceChange", source = "balanceDMS.clearBalance")
    @Mapping(target = "authBalanceChange", source = "balanceDMS.authBalance")
    BalanceAudit toBalanceAudit(BalanceDMS balanceDMS, UUID operationId);
}
