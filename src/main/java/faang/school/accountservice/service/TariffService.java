package faang.school.accountservice.service;

import faang.school.accountservice.dto.TariffDto;
import faang.school.accountservice.mappers.TariffMapper;
import faang.school.accountservice.model.RateHistory;
import faang.school.accountservice.model.Tariff;
import faang.school.accountservice.model.TariffHistory;
import faang.school.accountservice.repository.RateHistoryRepository;
import faang.school.accountservice.repository.TariffRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TariffService {
    private final TariffRepository tariffRepository;
    private final TariffMapper tariffMapper;
    private final RateHistoryRepository rateHistoryRepository;

    public TariffDto getTariff(Long id) {
        validateTariffId(id);
        Tariff tariff = tariffRepository.findById(id)
            .orElseThrow(() -> {
                log.error("Tariff with id {} not found", id);
                throw new EntityNotFoundException("Tariff with id " + id + " not found");
            });
        return tariffMapper.toDto(tariff);
    }

    @Transactional
    public TariffDto createTariff(TariffDto tariffDto) {
        validateTariffExistance(tariffDto.getId());
        Tariff tariff = tariffMapper.toEntity(tariffDto);
        RateHistory rateHistory = RateHistory.builder()
                .rate(tariffDto.getRate())
                .tariffId(tariffDto.getId())
                .startDate(LocalDateTime.now())
                .build();
        Tariff result = tariffRepository.save(tariff);
        rateHistoryRepository.save(rateHistory);
        return tariffMapper.toDto(result);
    }

    @Transactional
    public TariffDto updateTariff(Long id, TariffDto tariffDto) {
        validateTariffId(id);
        Tariff tariff = tariffRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Tariff with id {} not found", id);
                    throw new EntityNotFoundException("Tariff with id " + id + " not found");
                });
        List<RateHistory> rateHistories = rateHistoryRepository.findByTariffId(tariff.getId());
        Collections.sort(rateHistories, (r1, r2) -> r2.getStartDate().compareTo(r1.getStartDate()));
        RateHistory currentRateHistory = rateHistories.get(0);
        currentRateHistory.setEndDate(LocalDateTime.now());
        rateHistoryRepository.save(currentRateHistory);

        tariff.setRate(tariffDto.getRate());
        Tariff result = tariffRepository.save(tariff);

        RateHistory rateHistory = RateHistory.builder()
                .rate(tariffDto.getRate())
                .tariffId(tariffDto.getId())
                .startDate(LocalDateTime.now())
                .build();
        rateHistoryRepository.save(rateHistory);
        return tariffMapper.toDto(result);
    }

    private void validateTariffExistance(Long id) {
        if (tariffRepository.existsById(id)) {
            log.error("Tariff with id {} already exists", id);
            throw new EntityExistsException("Tariff with id " + id + " already exists");
        }
    }

    private void validateTariffId(Long id) {
        if (id == null || id <= 0) {
            log.info("Received a request to get a tariff with ID NULL or LESS THAN 0");
            throw new IllegalArgumentException("ERROR: ID cannot be NULL or LESS THAN 0");
        }
    }
}
