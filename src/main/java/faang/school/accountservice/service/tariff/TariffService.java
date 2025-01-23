package faang.school.accountservice.service.tariff;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.HistoryDto;
import faang.school.accountservice.dto.tariff.TariffCreateDto;
import faang.school.accountservice.dto.tariff.TariffDto;
import faang.school.accountservice.dto.tariff.TariffUpdateDto;
import faang.school.accountservice.entity.tariff.Tariff;
import faang.school.accountservice.mapper.tariff.TariffMapper;
import faang.school.accountservice.repository.tariff.TariffRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TariffService {
    private final TariffRepository tariffRepository;
    private final TariffMapper tariffMapper;
    private final ObjectMapper objectMapper;

    public List<TariffDto> getAllTariffs() {
        return tariffMapper.toDto(tariffRepository.findAll());
    }

    public Tariff findEntityById(Long id) {
        return tariffRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Tariff doesn't exist"));
    }

    public TariffDto findById(Long id) {
        return tariffMapper.toDto(findEntityById(id));
    }

    public TariffDto createTariff(TariffCreateDto createDto) {
        Tariff tariff = Tariff.builder()
                .name(createDto.getName())
                .rate(createDto.getRate())
                .rateHistory(createRateHistory(createDto.getRate()))
                .build();

        tariff = tariffRepository.save(tariff);
        log.info("Tariff '{}' created. Rate = {}", tariff.getName(), tariff.getRate());
        return tariffMapper.toDto(tariff);
    }

    @Transactional
    public TariffDto updateTariff(TariffUpdateDto updateDto) {
        Tariff tariff = findEntityById(updateDto.getId());
        tariff.setRateHistory(updateRateHistory(tariff.getRateHistory(), tariff.getRate(), updateDto.getRate()));
        tariff.setRate(updateDto.getRate());
        log.info("Tariff '{}' updated. New rate = {}", tariff.getName(), tariff.getRate());
        return tariffMapper.toDto(tariff);
    }

    private String createRateHistory(BigDecimal rate) {
        try {
            List<HistoryDto> history = List.of(new HistoryDto(null, rate.toString(), LocalDateTime.now()));
            return objectMapper.writeValueAsString(history);
        } catch (JsonProcessingException e) {
            log.error("Rate history creation error", e);
            throw new IllegalStateException("Rate history creation error", e);
        }
    }

    private String updateRateHistory(String history, BigDecimal oldRate, BigDecimal newRate) {
        try {
            List<HistoryDto> rateHistory = new ArrayList<>(Arrays.asList(objectMapper.readValue(history, HistoryDto[].class)));
            rateHistory.add(new HistoryDto(oldRate.toString(), newRate.toString(), LocalDateTime.now()));
            return objectMapper.writeValueAsString(rateHistory);
        } catch (JsonProcessingException e) {
            log.error("Rate history update error", e);
            throw new IllegalStateException("Rate history update error", e);
        }
    }

}
