package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.savings_account.OpenSavingsAccountDto;
import faang.school.accountservice.dto.savings_account.SavingsAccountResponseDto;
import faang.school.accountservice.entity.savings_account.SavingsAccount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = AccountMapper.class)
public interface SavingsAccountMapper {
    SavingsAccount toEntity(OpenSavingsAccountDto dto);

    @Mapping(target = "id", source = "entity.id")
    @Mapping(target = "currentRate", source = "entity.currentRate")
    @Mapping(target = "lastCalculationDate", source = "entity.lastCalculationDate")
    @Mapping(target = "account", source = "entity.account")
    @Mapping(target = "amount", source = "entity.amount")
    SavingsAccountResponseDto toResponseDto(SavingsAccount entity);
}
