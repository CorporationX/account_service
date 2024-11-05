package faang.school.accountservice.mapper.account;

import faang.school.accountservice.dto.account.SavingsAccountDto;
import faang.school.accountservice.entity.account.SavingsAccount;
import faang.school.accountservice.mapper.tariff.TariffMapper;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {AccountMapper.class, TariffMapper.class}, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface SavingsAccountMapper {

    @Mapping(source = "account.id", target = "accountId")
    @Mapping(source = "tariff", target = "tariffDto")
    SavingsAccountDto toDto(SavingsAccount savingsAccount);
}
