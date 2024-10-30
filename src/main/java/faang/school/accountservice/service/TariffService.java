package faang.school.accountservice.service;

import faang.school.accountservice.model.savings.Rate;
import faang.school.accountservice.model.savings.Tariff;
import faang.school.accountservice.model.savings.TariffType;
import faang.school.accountservice.repository.TariffHistoryRepository;
import faang.school.accountservice.repository.TariffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TariffService {
    private final TariffRepository tariffRepository;
    private final TariffHistoryRepository tariffHistoryRepository;

    @Transactional
    public Tariff createTariff(String tariffType, Double rateValue) {
        Tariff newTariff = initializeTariff(tariffType);
        addRateToTariff(newTariff, rateValue);
        return tariffRepository.save(newTariff);
    }

    @Transactional
    public Tariff updateTariffRate(UUID tariffId, Double newRateValue) {
        Tariff tariff = getTariffById(tariffId);
        addRateToTariff(tariff, newRateValue);
        return tariffRepository.save(tariff);
    }

    @Transactional(readOnly = true)
    public Tariff getTariffById(UUID tariffId) {
        return tariffRepository.findById(tariffId)
                .orElseThrow(() -> new RuntimeException("Tariff not found"));
    }

    private Tariff initializeTariff(String tariffType) {
        return tariffRepository.save(
                Tariff.builder()
                        .type(TariffType.valueOf(tariffType))
                        .rateHistory(new ArrayList<>())
                        .build()
        );
    }

    private void addRateToTariff(Tariff tariff, Double rateValue) {
        Rate newRate = Rate.builder()
                .rate(rateValue)
                .appliedAt(LocalDateTime.now())
                .tariff(tariff)
                .build();
        tariff.getRateHistory().add(newRate);
    }
}