package faang.school.accountservice.util.fabrics;

import faang.school.accountservice.entity.tariff.TariffRate;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TariffRateFabric {
    private static final Double RATE = 0.01;

    public static TariffRate buildTariffRate() {
        return TariffRate.builder()
                .rate(RATE)
                .build();
    }
}
