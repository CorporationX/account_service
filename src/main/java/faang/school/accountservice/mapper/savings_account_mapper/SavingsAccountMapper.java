package faang.school.accountservice.mapper.savings_account_mapper;

import faang.school.accountservice.dto.savings_account.SavingsAccountRequestDto;
import faang.school.accountservice.dto.savings_account.SavingsAccountResponseDto;
import faang.school.accountservice.mapper.tariff.TariffMapper;
import faang.school.accountservice.model.savings_account.SavingsAccount;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", uses = TariffMapper.class, injectionStrategy = InjectionStrategy.CONSTRUCTOR, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SavingsAccountMapper {
    @Mapping(target = "savingsAccountId", source = "account.id")
    @Mapping(target = "tariffAndInterestRate", source = "tariff")
    SavingsAccountResponseDto toDto(SavingsAccount savingsAccount);

    @Mapping(target = "accountId", source = "account.id")
    @Mapping(target = "tariffAndInterestRate", source = "tariff")
    List<SavingsAccountResponseDto> toDto(List<SavingsAccount> savingsAccounts);

    @Mapping(target = "account", ignore = true)
    @Mapping(target = "tariff", ignore = true)
    SavingsAccount toEntity(SavingsAccountRequestDto savingsAccountRequestDto);
}