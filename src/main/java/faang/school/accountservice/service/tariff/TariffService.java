package faang.school.accountservice.service.tariff;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.tariff.TariffDto;
import faang.school.accountservice.dto.tariff.TariffRequestDto;
import faang.school.accountservice.entity.rate.Rate;
import faang.school.accountservice.entity.tariff.Tariff;
import faang.school.accountservice.mapper.tariff.TariffMapper;
import faang.school.accountservice.repository.tariff.TariffRepository;
import faang.school.accountservice.service.rate.RateService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class TariffService {

    private final TariffRepository tariffRepository;
    private final RateService rateService;
    private final TariffMapper tariffMapper;
    private final ObjectMapper objectMapper;

    @Transactional
    public TariffDto createTariff(TariffRequestDto tariffRequestDto) {
        log.info("start createTariff with dto: {}", tariffRequestDto.toString());
        Rate existingRate = rateService.getOrCreateRateByInterestRate(tariffRequestDto.getInterestRate());

        Tariff tariff = Tariff.builder()
                .tariffName(tariffRequestDto.getTariffName())
                .rate(existingRate)
                .build();

        addRateToHistory(tariff, tariffRequestDto);

        Tariff createdTariff = tariffRepository.save(tariff);
        log.info("finish createTariff with entity: {}", createdTariff);

        return tariffMapper.toDto(createdTariff);
    }

    public List<TariffDto> getAllTariffs() {
        log.info("start getAllTariffs");
        return tariffMapper.toDtos(tariffRepository.findAll());
    }

    @Transactional
    public TariffDto updateTariffRate(TariffRequestDto tariffRequestDto) {
        log.info("start updateTariffRate with dto: {}", tariffRequestDto.toString());
        Tariff existingTariff = tariffRepository.findByTariffName(tariffRequestDto.getTariffName())
                .orElseThrow(() -> new NoSuchElementException("Tariff with type - "
                        + tariffRequestDto.getTariffName() + " does not exist"));

        Rate existingRate = rateService.getOrCreateRateByInterestRate(tariffRequestDto.getInterestRate());
        existingTariff.setRate(existingRate);

        addRateToHistory(existingTariff, tariffRequestDto);

        Tariff updatedTariff = tariffRepository.save(existingTariff);
        log.info("finish updateTariffRate with entity: {}", updatedTariff);

        return tariffMapper.toDto(updatedTariff);
    }

    public Tariff getTariffByTariffType(String tariffType) {
        log.info("start getTariffByTariffType with tariffType: {}", tariffType);
        return tariffRepository.findByTariffName(tariffType)
                .orElseThrow(() -> new NoSuchElementException("Tariff with type - "
                        + tariffType + " does not exist"));
    }

    private void addRateToHistory(Tariff tariff, TariffRequestDto tariffRequestDto) {
        try {
            List<Double> rates = new ArrayList<>();

            if (tariff.getRateHistory() != null) {
                rates = objectMapper.readValue(tariff.getRateHistory(), new TypeReference<>() {
                });
            }
            rates.add(tariffRequestDto.getInterestRate());

            tariff.setRateHistory(objectMapper.writeValueAsString(rates));
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }
    }
}
