package faang.school.accountservice.util.fabrics;

import faang.school.accountservice.entity.tariff.Tariff;
import lombok.experimental.UtilityClass;

import java.util.List;

import static faang.school.accountservice.util.fabrics.TariffRateFabric.buildTariffRate;

@UtilityClass
public class TariffFabric {
    public static Tariff buildTariff() {
        return Tariff.builder()
                .tariffRates(List.of(buildTariffRate()))
                .build();
    }

    public static Tariff buildTariff(Long id) {
        return Tariff.builder()
                .id(id)
                .tariffRates(List.of(buildTariffRate()))
                .build();
    }
}
