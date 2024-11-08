package faang.school.accountservice.service.cashback;

import faang.school.accountservice.dto.CashbackTariffDto;
import faang.school.accountservice.dto.CashbackMappingDto;

public interface CashbackTariffService {
    CashbackTariffDto getTariff(long id);
    CashbackTariffDto createTariff(CashbackTariffDto cashbackTariffDto);
    CashbackTariffDto updateTariff(CashbackTariffDto cashbackTariffDto, long id);
    void deleteTariff(long id);
    CashbackMappingDto getCashbackMapping(CashbackMappingDto cashbackMappingDto);
    CashbackMappingDto createCashbackMapping(CashbackMappingDto cashbackMappingDto);
    void updateCashbackMapping(CashbackMappingDto cashbackMappingDto);
    void deleteCashbackMapping(CashbackMappingDto cashbackMappingDto);
}
