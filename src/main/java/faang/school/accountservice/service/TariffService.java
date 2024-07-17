package faang.school.accountservice.service;

import faang.school.accountservice.dto.TariffDto;
import faang.school.accountservice.exception.EntityNotFoundException;
import faang.school.accountservice.mapper.TariffMapper;
import faang.school.accountservice.model.RateHistory;
import faang.school.accountservice.model.Tariff;
import faang.school.accountservice.repository.RateHistoryRepository;
import faang.school.accountservice.repository.TariffRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TariffService {
    private final TariffRepository tariffRepository;
    private final TariffMapper tariffMapper;
    private final RateHistoryRepository rateHistoryRepository;

    @Transactional
    public TariffDto createTariff(String name, List<Double> rateHistory) {
        log.info("Trying to create tariff with name - {}", name);
        Tariff tariff = new Tariff();
        tariff.setName(name);
        tariff.setRateHistory(rateHistory.stream()
                .map(rate -> {
                    RateHistory rateHistory1 = new RateHistory();
                    rateHistory1.setRate(rate);
                    rateHistory1.setTariff(tariff);
                    return rateHistory1;
                }).toList());
        tariff.setCreatedAt(LocalDateTime.now());
        tariff.setUpdatedAt(LocalDateTime.now());
        tariffRepository.save(tariff);
        return tariffMapper.toDto(tariff);
    }

    @Transactional
    public TariffDto updateTariff(Long tariffId, Double newRate) {
        log.info("Trying updated tariff with id - {}", tariffId);
        TariffDto tariffDto = getTariffById(tariffId);
        Tariff tariff = tariffMapper.toEntity(tariffDto);

        List<RateHistory> rateHistoryList = new ArrayList<>(tariff.getRateHistory());
        RateHistory rateHistory = RateHistory.builder()
                .rate(newRate)
                .tariff(tariff)
                .build();
        rateHistoryList.add(rateHistory);

        tariff.setRateHistory(rateHistoryList);
        tariff.setUpdatedAt(LocalDateTime.now());
        tariffRepository.save(tariff);

        return tariffDto;
    }

    public TariffDto getTariffById(Long tariffId) {
        log.info("Getting tariff by id - {}", tariffId);
        Tariff tariff = tariffRepository.findById(tariffId)
                .orElseThrow(() -> {
                    log.error("There is no tariff under this ID - {}", tariffId);
                    return new EntityNotFoundException(String.format("There is no tariff under this ID - %s", tariffId));
                });
        return tariffMapper.toDto(tariff);
    }
}
