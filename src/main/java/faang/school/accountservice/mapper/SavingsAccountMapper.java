package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.savingsAccounts.SavingsAccountCreateDto;
import faang.school.accountservice.dto.savingsAccounts.SavingsAccountReadDto;
import faang.school.accountservice.entity.SavingsAccount;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SavingsAccountMapper {
    SavingsAccountReadDto toReadDto(SavingsAccount savingsAccount);

    SavingsAccount toEntity(SavingsAccountCreateDto dto);
}
