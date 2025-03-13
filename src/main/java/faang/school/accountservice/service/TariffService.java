package faang.school.accountservice.service;

import faang.school.accountservice.entity.Tariff;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface TariffService {
    Tariff createTariff(String name, BigDecimal initialRate);
    Tariff addRateToTariff(Long tariffId, BigDecimal newRate);
    List<Tariff> getAllTariffs();
    Optional<Tariff> getTariff(Long id);
    Tariff updateTariffName(Long tariffId, String newName);

    Tariff updateTariffRate(Long id, BigDecimal newRate);
}