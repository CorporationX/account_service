package faang.school.accountservice.service.tariff;

import faang.school.accountservice.dto.tariff.TariffCreationDto;
import faang.school.accountservice.dto.tariff.TariffResponseDto;

import java.math.BigDecimal;
import java.util.List;

public interface TariffService {
    TariffResponseDto createTariff(TariffCreationDto dto);
    TariffResponseDto updateTariffRate(Long tariffId, BigDecimal newRate);
    TariffResponseDto getTariff(Long tariffId);
    List<TariffResponseDto> getAllTariffs();
    boolean existsTariffById(Long tariffId);
}
