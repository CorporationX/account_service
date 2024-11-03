package faang.school.accountservice.service;

import faang.school.accountservice.dto.CashbackBalanceDto;
import faang.school.accountservice.dto.CashbackTariffDto;

public interface CashbackTariffService {

    CashbackTariffDto createCashbackTariff(CashbackTariffDto cashbackTariffDto);

    CashbackTariffDto getCashbackTariffById(long id);

    int earnCashbackOnExpensesAllAccounts();

    void earnCashbackOnExpenses(CashbackBalanceDto cashbackBalanceDto);
}
