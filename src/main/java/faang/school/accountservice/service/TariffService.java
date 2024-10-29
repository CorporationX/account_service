package faang.school.accountservice.service;

import faang.school.accountservice.model.dto.TariffDto;

public interface TariffService {
    TariffDto createTariff(TariffDto tariffDto);
    TariffDto updateTariff(Long id, Double rate);
}
