package faang.school.accountservice.service;

import faang.school.accountservice.dto.TariffAndRateDto;
import faang.school.accountservice.entity.RateHistory;
import faang.school.accountservice.repository.RateHistoryRepository;
import faang.school.accountservice.repository.TariffRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TariffService {
    private final TariffRepository tariffRepository;
    private final RateHistoryRepository rateHistoryRepository;

    @Transactional
    public TariffAndRateDto addTariffRate(String accountNumber, Double tariffRate) {
        var tariff = tariffRepository.findTariffByAccountNumber(accountNumber).orElseThrow();
        var rateHistory = RateHistory.builder()
                .tariff(tariff)
                .rate(tariffRate)
                .createdAt(LocalDateTime.now())
                .build();
        var savedRateHistory = rateHistoryRepository.save(rateHistory);
        tariff.getRateHistoryList().add(savedRateHistory);

        var updatedTariff = tariffRepository.save(tariff);

        var rateHistoryListOfCurrentTariff = updatedTariff.getRateHistoryList();
        return TariffAndRateDto.builder()
                .tariffType(updatedTariff.getType().name())
                .rate(rateHistoryListOfCurrentTariff.get(rateHistoryListOfCurrentTariff.size() - 1).getRate())
                .build();
    }

    public List<Double> getTariffRates(String accountNumber) {
        return tariffRepository.findTariffWithRateHistoryByAccountNumber(accountNumber)
                .orElseThrow()
                .getRateHistoryList()
                .stream()
                .map(RateHistory::getRate)
                .toList();
    }
}

