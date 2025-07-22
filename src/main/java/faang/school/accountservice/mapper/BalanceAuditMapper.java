package faang.school.accountservice.mapper;

import faang.school.accountservice.model.AccountBalance;
import faang.school.accountservice.model.BalanceAudit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;
import java.util.UUID;

@Mapper(componentModel = "spring",
        imports = { LocalDateTime.class })
public interface BalanceAuditMapper {
    @Mapping(source = "balance.account",             target = "account")
    @Mapping(source = "balance.version",             target = "version")
    @Mapping(source = "balance.authorizedBalance",   target = "authorizedBalance")
    @Mapping(source = "balance.actualBalance",       target = "actualBalance")
    @Mapping(source = "operationId",                 target = "operationId")
    @Mapping(target = "createdAt",                   expression = "java(LocalDateTime.now())")
    BalanceAudit toAudit(AccountBalance balance, UUID operationId);
}
