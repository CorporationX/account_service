package faang.school.accountservice.mapper;

import faang.school.accountservice.model.AccountBalance;
import faang.school.accountservice.model.BalanceAudit;
import faang.school.accountservice.model.dto.AccountBalanceDto;
import faang.school.accountservice.model.dto.BalanceAuditDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountServiceMapper {

    AccountBalanceDto toDto(AccountBalance entity);

    BalanceAuditDto toDto(BalanceAudit entity);

    List<BalanceAuditDto> toDtoList(List<BalanceAudit> entities);
}