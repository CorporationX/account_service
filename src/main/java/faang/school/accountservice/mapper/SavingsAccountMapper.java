package faang.school.accountservice.mapper;

import faang.school.accountservice.model.dto.SavingsAccountDto;
import faang.school.accountservice.model.entity.SavingsAccount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface SavingsAccountMapper {

    @Mapping(source = "tariff.id", target = "tariffId")
    SavingsAccountDto savingsAccountToSavingsAccountDto(SavingsAccount savingsAccount);

    SavingsAccount savingsAccountDtoToSavingsAccount(SavingsAccountDto savingsAccountDto);

}
