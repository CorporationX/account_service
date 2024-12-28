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
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class SavingAccountMapper {
    @Autowired
    private TariffMapper tariffMapper;
    @Autowired
    private AccountMapper accountMapper;

    public abstract SavingAccountDto toDto(SavingAccount entity);

    public abstract List<SavingAccountDto> toDto(List<SavingAccount> entity);


    protected TariffDto mapTariff(Tariff tariff) {
        return tariffMapper.toDto(tariff);
    }

    protected AccountDto mapAccount(Account account) {
        return accountMapper.toDto(account);
    }

}
