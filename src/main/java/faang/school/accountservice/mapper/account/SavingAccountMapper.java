package faang.school.accountservice.mapper.account;

import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.dto.account.saving.SavingAccountDto;
import faang.school.accountservice.dto.tariff.TariffDto;
import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.entity.account.SavingAccount;
import faang.school.accountservice.entity.tariff.Tariff;
import faang.school.accountservice.mapper.tariff.TariffMapper;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SavingAccountMapper {
    TariffMapper tariffMapper = Mappers.getMapper(TariffMapper.class);
    AccountMapper accountMapper = Mappers.getMapper(AccountMapper.class);

    SavingAccountDto toDto(SavingAccount entity);

    List<SavingAccountDto> toDto(List<SavingAccount> entity);


    default TariffDto mapTariff(Tariff tariff) {
        return tariffMapper.toDto(tariff);
    }

    default AccountDto mapAccount(Account account) {
        return accountMapper.toDto(account);
    }

}
