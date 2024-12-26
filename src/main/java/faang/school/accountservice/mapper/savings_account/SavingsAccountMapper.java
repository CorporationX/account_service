package faang.school.accountservice.mapper.savings_account;

import faang.school.accountservice.dto.savings_account.SavingsAccountResponse;
import faang.school.accountservice.entity.savings_account.SavingsAccount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SavingsAccountMapper {

    @Mapping(source = "account.id", target = "baseAccountId")
    SavingsAccountResponse toResponse(SavingsAccount savingsAccount);
}
