package faang.school.accountservice.mappers;

import faang.school.accountservice.dto.SavingsAccountDto;
import faang.school.accountservice.model.SavingsAccount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SavingsAccountMapper {
    @Mapping(source = "account.id", target = "accountId")
    SavingsAccountDto toDto(SavingsAccount savingsAccount);

    @Mapping(target ="tariffs", ignore = true)
    @Mapping(source = "accountId", target = "account.id")
    SavingsAccount toEntity(SavingsAccountDto savingsAccountDto);
}
