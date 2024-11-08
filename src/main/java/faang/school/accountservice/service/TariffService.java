package faang.school.accountservice.service;


import faang.school.accountservice.dto.SavingsAccountDto;
import faang.school.accountservice.dto.TariffDto;
import faang.school.accountservice.enums.TariffType;

import java.math.BigDecimal;
import java.util.List;

public interface TariffService {

    TariffDto addTariff(TariffDto tariffDto, SavingsAccountDto accountDto);

    TariffDto changeRateTariff(Long id, BigDecimal newRate);

    List<TariffType> getAllTariffs();
}
