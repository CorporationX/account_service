package faang.school.accountservice.mapper;

import ch.qos.logback.core.model.ComponentModel;
import faang.school.accountservice.dto.AccountResponse;
import faang.school.accountservice.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountMapper {

    AccountResponse toDto(Account account);

    List<AccountResponse> toDtoList(List<Account> accounts);
}
