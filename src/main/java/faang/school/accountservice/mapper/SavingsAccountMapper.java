package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.SavingsAccountDto;
import faang.school.accountservice.model.SavingsAccount;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface SavingsAccountMapper {
    SavingsAccountDto toDto(SavingsAccount savingsAccount);

    SavingsAccount toEntity(SavingsAccountDto savingsAccountDto);
}
