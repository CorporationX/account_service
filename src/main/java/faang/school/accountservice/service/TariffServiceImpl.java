package faang.school.accountservice.service;

import faang.school.accountservice.entity.Tariff;
import faang.school.accountservice.exception.TariffNotFoundException;
import faang.school.accountservice.repository.TariffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TariffServiceImpl implements TariffService {
    private static final int RATE_SCALE = 4;

    private final TariffRepository tariffRepository;

    @Override
    @Transactional
    public Tariff createTariff(String name, BigDecimal initialRate) {
        validateRate(initialRate);

        Tariff tariff = Tariff.builder()
                .name(name)
                .rateHistory(List.of(convertRate(initialRate)))
                .build();

        return tariffRepository.save(tariff);
    }

    @Override
    @Transactional
    public Tariff addRateToTariff(Long tariffId, BigDecimal newRate) {
        validateRate(newRate);

        Tariff tariff = tariffRepository.findById(tariffId)
                .orElseThrow(() -> new TariffNotFoundException(tariffId));

        List<Double> updatedRates = tariff.getRateHistory();
        updatedRates.add(convertRate(newRate));
        tariff.setRateHistory(updatedRates);

        return tariffRepository.save(tariff);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Tariff> getAllTariffs() {
        return tariffRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Tariff> getTariff(Long id) {
        return tariffRepository.findById(id);
    }

    @Override
    @Transactional
    public Tariff updateTariffName(Long tariffId, String newName) {
        Tariff tariff = tariffRepository.findById(tariffId)
                .orElseThrow(() -> new TariffNotFoundException(tariffId));
        tariff.setName(newName);
        return tariffRepository.save(tariff);
    }

    @Override
    @Transactional
    public Tariff updateTariffRate(Long id, BigDecimal newRate) {
        Tariff tariff = tariffRepository.findById(id)
                .orElseThrow(() -> new TariffNotFoundException(id));

        validateRate(newRate);

        List<Double> updatedRates = new ArrayList<>(tariff.getRateHistory());
        updatedRates.add(convertRate(newRate));
        tariff.setRateHistory(updatedRates);

        return tariffRepository.save(tariff);
    }

    private Double convertRate(BigDecimal rate) {
        return rate.setScale(RATE_SCALE, RoundingMode.HALF_UP)
                .doubleValue();
    }

    private void validateRate(BigDecimal rate) {
        if (rate.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Rate cannot be negative");
        }
        if (rate.scale() > RATE_SCALE) {
            throw new IllegalArgumentException("Rate precision exceeds allowed scale");
        }
    }
}