package faang.school.accountservice.service;


import faang.school.accountservice.dto.TariffDto;
import faang.school.accountservice.enums.TariffType;

import java.util.List;

public interface TariffService {

    TariffDto addTariff(TariffDto tariffDto);

    TariffDto changeRateTariffAndSaveHistory(Long id, Double newRate);

    List<TariffType> getAllTariffs();
}
