package faang.school.accountservice.service.tariff;

import faang.school.accountservice.dto.tariff.CreateTariffDto;
import faang.school.accountservice.dto.tariff.TariffDto;
import faang.school.accountservice.dto.tariff.UpdateTariffDto;

import java.math.BigDecimal;
import java.util.List;

public interface TariffService {

    TariffDto createTariff(CreateTariffDto tariffDto);

    TariffDto updateTariffRate(UpdateTariffDto tariffDto);

    TariffDto getTariff(Long id);
}
