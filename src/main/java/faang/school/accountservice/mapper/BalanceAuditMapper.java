package faang.school.accountservice.mapper;

import faang.school.accountservice.model.Balance;
import faang.school.accountservice.model.BalanceAudit;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface BalanceAuditMapper {
    BalanceAudit toBalanceAudit(Balance balance);

    default BalanceAudit fromBalance(Balance balance, Long operationId){
        return BalanceAudit.builder()
                .accountNumber(balance.getAccount().getNumber())
                .authorizedAmount(balance.getAuthorizedBalance())
                .currentAmount(balance.getCurrentBalance())
                .operationId(operationId)
                .balanceVersion(balance.getVersion())
                .build();
    }
}
