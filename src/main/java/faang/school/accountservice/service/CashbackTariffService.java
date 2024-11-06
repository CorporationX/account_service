package faang.school.accountservice.service;

import faang.school.accountservice.model.cashback.CreateCashbackTariffDto;
import faang.school.accountservice.model.cashback.ReadCashbackTariffDto;

public interface CashbackTariffService {

    ReadCashbackTariffDto createTariff(CreateCashbackTariffDto createCashbackTariffDto);

    ReadCashbackTariffDto getTariff(Long tariffId);
}
