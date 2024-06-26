package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.savings.SavingsAccountDto;
import faang.school.accountservice.model.SavingsAccount;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SavingsAccountMapper {

    SavingsAccount toEntity(SavingsAccountDto savingsAccountDto);

    SavingsAccountDto toDto(SavingsAccount savingsAccount);

    void update(SavingsAccountDto savingsAccountDto, @MappingTarget SavingsAccount savingsAccount);
}

