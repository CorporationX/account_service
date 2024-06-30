package faang.school.accountservice.mapper;

import faang.school.accountservice.model.Balance;
import faang.school.accountservice.model.BalanceAudit;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface BalanceAuditMapper {
    @Mapping(source = "account.number", target = "accountNumber", numberFormat = "#")
    @Mapping(source = "authorizedBalance", target = "authorizedAmount")
    @Mapping(source = "currentBalance", target = "currentAmount")
    @Mapping(source = "version", target = "balanceVersion")
    BalanceAudit fromBalance(Balance balance);

    @AfterMapping
    default void setCreatedAt(@MappingTarget BalanceAudit balanceAudit) {
        balanceAudit.setCreatedAt(LocalDateTime.now());
    }
}
