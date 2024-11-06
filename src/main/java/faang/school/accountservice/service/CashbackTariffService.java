package faang.school.accountservice.service;

import faang.school.accountservice.dto.TariffDto;
import faang.school.accountservice.dto.TypeMappingDto;

import java.util.List;

public interface CashbackTariffService {
    TariffDto getTariff(long id);
    TariffDto createTariff(TariffDto tariffDto);
    TariffDto updateTariff(TariffDto tariffDto, long id);
    void deleteTariff(long id);
    TypeMappingDto getCashbackMapping(TypeMappingDto typeMappingDto);
    TypeMappingDto createCashbackMapping(TypeMappingDto typeMappingDto);
    void updateCashbackMapping(TypeMappingDto typeMappingDto);
    void deleteCashbackMapping(TypeMappingDto typeMappingDto);
}
