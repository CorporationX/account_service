package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.entity.BalanceAudit;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BalanceMapper {

    @Mapping(source = "accountId", target = "account.id")
    @Mapping(target = "account", ignore = true)
    Balance toEntity(BalanceDto balanceDto);

    @Mapping(source = "account.id", target = "accountId")
    BalanceDto toDto(Balance balance);

    @Mapping(target = "currentAuthorizationBalance", source = "currentAuthorizationBalance")
    @Mapping(target = "currentActualBalance", source = "currentActualBalance")
    void updateBalanceFromDto(BalanceDto balanceDto, @MappingTarget Balance balance);

    @Mapping(source = "balance.version", target = "balanceVersion", qualifiedByName = "balanceVersionMap")
    @Mapping(source = "balance.currentAuthorizationBalance", target = "currentAuthorizationBalance")
    @Mapping(source = "balance.currentActualBalance", target = "currentActualBalance")
    @Mapping(target = "balance", expression = "java(balance)")
    @Mapping(target = "operationId", source = "operationId")
    @Mapping(target = "id", ignore = true)
    BalanceAudit toBalanceAudit(Balance balance, long operationId);

    @Named("balanceVersionMap")
    default long balanceVersionMap(long version) {
        return version + 1;
    }
}
