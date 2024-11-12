package faang.school.accountservice.entity.tariff;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TariffRateBuilder {
    public static TariffRate build(Tariff tariff, Double rate) {
        return TariffRate.builder()
                .tariff(tariff)
                .rate(rate)
                .build();
    }
}
