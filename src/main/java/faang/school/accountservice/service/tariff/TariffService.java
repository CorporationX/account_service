package faang.school.accountservice.service.tariff;

import faang.school.accountservice.model.Tariff;

import java.math.BigDecimal;
import java.util.List;

public interface TariffService {

    Tariff createTariff(String name, List<BigDecimal> rateHistory);

    Tariff updateTariffRate(Long id, BigDecimal newRate);

    Tariff getTariff(Long id);
}
