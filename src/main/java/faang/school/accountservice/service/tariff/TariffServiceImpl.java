package faang.school.accountservice.service.tariff;

import faang.school.accountservice.exception.NotFoundException;
import faang.school.accountservice.model.Tariff;
import faang.school.accountservice.repository.TariffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TariffServiceImpl implements TariffService {

    private TariffRepository tariffRepository;

    @Override
    @Transactional
    public Tariff createTariff(String name, List<BigDecimal> rateHistory) {
        Tariff tariff = new Tariff();
        tariff.setName(name);
        tariff.setRateHistory(rateHistory);

        return tariffRepository.save(tariff);
    }

    @Override
    @Transactional
    public Tariff updateTariffRate(Long id, BigDecimal newRate) {
        Tariff tariff = tariffRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tariff not found"));

        List<BigDecimal> rateHistory = new ArrayList<>(tariff.getRateHistory());
        rateHistory.add(newRate);
        tariff.setRateHistory(rateHistory);

        return tariffRepository.save(tariff);
    }

    @Override
    public Tariff getTariff(Long id) {
        return tariffRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tariff not found"));
    }

}
