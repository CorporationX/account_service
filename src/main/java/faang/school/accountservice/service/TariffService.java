package faang.school.accountservice.service;

import faang.school.accountservice.exception.ResourceNotFoundException;
import faang.school.accountservice.model.Tariff;
import faang.school.accountservice.model.TariffRate;
import faang.school.accountservice.model.enumeration.TariffType;
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
        TariffRate tariffRate = TariffRate.builder().tariff(tariff).rate(rate).build();
        tariff.getTariffRates().add(tariffRate);
        return tariffRepository.save(tariff);
    }

    @Transactional
    public Tariff updateRate(Long tariffId, Double newRate) {
        Tariff foundTariff = findById(tariffId);
        TariffRate tariffRate = TariffRate.builder().tariff(foundTariff).rate(newRate).build();
        foundTariff.getTariffRates().add(tariffRate);
        return tariffRepository.save(foundTariff);
    }

    @Transactional(readOnly = true)
    public Tariff findById(Long id) {
        return tariffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tariff", id));
    }

    @Transactional(readOnly = true)
    public Tariff findByType(TariffType type) {
        return tariffRepository.findByTariffType(type)
                .orElseThrow(() -> new ResourceNotFoundException("Tariff", type));
    }

    @Transactional(readOnly = true)
    public List<Tariff> findAll() {
        return tariffRepository.findAll();
    }
}
