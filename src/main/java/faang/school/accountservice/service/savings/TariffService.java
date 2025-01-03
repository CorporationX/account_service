package faang.school.accountservice.service.savings;

import faang.school.accountservice.dto.savings.TariffDto;
import faang.school.accountservice.dto.savings.TariffRateHistoryDto;
import faang.school.accountservice.model.savings.Tariff;
import jakarta.validation.Valid;

public interface TariffService {

  TariffRateHistoryDto add(Long userId, @Valid TariffDto dto);

  TariffRateHistoryDto getById(Long userId, @Valid Long id);

  TariffRateHistoryDto update(Long userId, TariffDto dto);

  Tariff findById(Long id);
}
