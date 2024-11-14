package faang.school.accountservice.service.tariff;

import faang.school.accountservice.entity.tariff.Tariff;
import faang.school.accountservice.entity.tariff.TariffRate;
import faang.school.accountservice.exception.ResourceNotFoundException;
import faang.school.accountservice.repository.TariffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class TariffService {
    private final TariffRepository tariffRepository;

    @Transactional
    public Tariff create(Tariff tariff, Double rate) {
        TariffRate tariffRate = build(tariff, rate);
        tariff.getTariffRates().add(tariffRate);
        return tariffRepository.save(tariff);
    }

    @Transactional
    public Tariff updateRate(Long tariffId, Double newRate) {
        Tariff foundTariff = findById(tariffId);
        TariffRate tariffRate = build(foundTariff, newRate);
        foundTariff.getTariffRates().add(tariffRate);
        return tariffRepository.save(foundTariff);
    }

    @Transactional(readOnly = true)
    public Tariff findById(Long id) {
        return tariffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Tariff.class, id));
    }

    @Transactional(readOnly = true)
    public List<Tariff> findAll() {
        return tariffRepository.findAll();
    }

    private TariffRate build(Tariff tariff, Double rate) {
        return TariffRate.builder()
                .tariff(tariff)
                .rate(rate)
                .build();
    }
}
