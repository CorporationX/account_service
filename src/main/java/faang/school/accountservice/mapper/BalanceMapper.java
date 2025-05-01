package faang.school.accountservice.mapper;

import faang.school.accountservice.model.Balance;
import faang.school.accountservice.model.BalanceAudit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface BalanceMapper {

    @Mapping(target = "paymentOperationId", source = "operationId")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "clearBalanceChange", source = "balance.clearBalance")
    @Mapping(target = "authBalanceChange", source = "balance.authBalance")
    BalanceAudit toBalanceAudit(Balance balance, UUID operationId);
}
