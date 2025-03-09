package faang.school.accountservice.service.savings;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.TariffDto;
import faang.school.accountservice.entity.Tariff;
import faang.school.accountservice.exception.EntityNotFoundException;
import faang.school.accountservice.mapper.TariffMapper;
import faang.school.accountservice.repository.TariffRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TariffService {
    private final TariffRepository tariffRepository;
    private final ObjectMapper objectMapper;
    private final TariffMapper tariffMapper;

    @Transactional
    public TariffDto addTariff(String name, List<Double> rates) {
        try {
            String rateHistoryJson = objectMapper.writeValueAsString(rates != null ? rates : Collections.emptyList());

            Tariff tariff = Tariff.builder()
                    .tariffName(name)
                    .rateHistory(rateHistoryJson)
                    .build();

            tariffRepository.save(tariff);
            return tariffMapper.toDto(tariff);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Ошибка во время записи тарифа", e);
        }
    }

    @Transactional
    public TariffDto updateTariffRate(Long id, double newRate) {
        Tariff tariff = tariffRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Тарифа с id " + id + " не существует"));

        try {
            List<Double> tariffRates = tariff.getRateHistory() != null && !tariff.getRateHistory().isBlank()
                    ? objectMapper.readValue(tariff.getRateHistory(), new TypeReference<>() {})
                    : new ArrayList<>();

            tariffRates.add(newRate);

            tariff.setRateHistory(objectMapper.writeValueAsString(tariffRates));

            tariffRepository.save(tariff);
            return tariffMapper.toDto(tariff);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Ошибка при обновлении тарифа", e);
        }
    }

    public List<TariffDto> getAllTariffs() {
        return tariffRepository.findAll()
                .stream()
                .map(tariffMapper::toDto)
                .toList();
    }

    public TariffDto getTariffById(Long id) {
        Tariff tariff = tariffRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Тарифа с id " + id + " не существует"));
        return tariffMapper.toDto(tariff);
    }
}
