package faang.school.accountservice.mappers;

import faang.school.accountservice.dto.SavingsAccountDto;
import faang.school.accountservice.model.SavingsAccount;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SavingsAccountMapper {
    SavingsAccountDto toDto(SavingsAccount savingsAccount);
    SavingsAccount toEntity(SavingsAccountDto savingsAccountDto);
}
