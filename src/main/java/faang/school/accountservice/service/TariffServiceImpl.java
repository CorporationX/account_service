package faang.school.accountservice.service;

import faang.school.accountservice.dto.TariffDto;
import faang.school.accountservice.entity.Tariff;
import faang.school.accountservice.enums.TariffType;
import faang.school.accountservice.mapper.TariffMapper;
import faang.school.accountservice.repository.TariffRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TariffServiceImpl implements TariffService {

    private final TariffRepository tariffRepository;
    private final TariffMapper tariffMapper;

    @Override
    public TariffDto addTariff(TariffDto tariffDto) {
        tariffRepository.save(tariffMapper.toEntity(tariffDto));
        return tariffDto;
    }

    @Override
    public TariffDto changeRateTariff(Long id, BigDecimal newRate) {
        Tariff tariff = tariffRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Tariff not found with id " + id));

        saveToRateHistory(tariff);
        tariff.setCurrentRate(newRate);

        return tariffMapper.toDto(tariffRepository.save(tariff));
    }

    @Override
    public List<TariffType> getAllTariffs() {
        return Arrays.asList(TariffType.values());
    }

    private void saveToRateHistory(Tariff tariff) {
        tariff.getBettingHistory().add(tariff.getCurrentRate());
    }
}
