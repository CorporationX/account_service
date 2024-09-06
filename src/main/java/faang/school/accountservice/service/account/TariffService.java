package faang.school.accountservice.service.account;

import faang.school.accountservice.dto.account.TariffDto;
import faang.school.accountservice.entity.account.Tariff;
import faang.school.accountservice.mapper.account.TariffMapper;
import faang.school.accountservice.repository.account.TariffRepository;
import faang.school.accountservice.validator.TariffValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TariffService {

    private final TariffMapper tariffMapper;
    private final TariffRepository tariffRepository;
    private final TariffValidator tariffValidator;

    @Transactional
    public TariffDto createTariff(TariffDto tariffDto, String rate) {
        Tariff tariff = tariffMapper.toEntity(tariffDto);

        String rateHistory = formatRateHistory(tariff.getRateHistory(), rate);
        tariff.setRateHistory(rateHistory);

        tariff = tariffRepository.save(tariff);
        return tariffMapper.toDto(tariff);
    }

    @Transactional
    public TariffDto updateTariffHistory(UUID tariffId, String rate) {
        Tariff tariff = getTariff(tariffId);

        tariffValidator.validateRateForUpdates(rate, getRate(tariff.getRateHistory()));
        String rateHistory = formatRateHistory(tariff.getRateHistory(), rate);
        tariff.setRateHistory(rateHistory);

        tariff = tariffRepository.save(tariff);
        return tariffMapper.toDto(tariff);
    }

    @Transactional(readOnly = true)
    public String getRateHistoryById(UUID id) {
        Tariff tariff = getTariff(id);
        return tariff.getRateHistory();
    }

    @Transactional(readOnly = true)
    public String getRateByById(UUID id) {
        Tariff tariff = getTariff(id);
        return getRate(tariff.getRateHistory());
    }

    @Transactional(readOnly = true)
    public TariffDto getTariffById(UUID id) {
        Tariff tariff = getTariff(id);
        return tariffMapper.toDto(tariff);
    }

    private String formatRateHistory(String rateHistory, String rate) {
        if(rateHistory == null || rateHistory.isBlank()) {
            rateHistory = "[" + rate + "]";
            return rateHistory;
        }

        rateHistory = rateHistory.replaceAll("]", "," + rate + "]");
        return rateHistory;
    }

    private String getRate(String rateHistory) {
        if(rateHistory.contains(",")) {
            String[] rates = rateHistory.split(",");
            int lastTariffIndex = rates.length - 1;
            return rates[lastTariffIndex].substring(0, rates[lastTariffIndex].length() - 1);
        }
        else {
            return rateHistory.substring(1, rateHistory.length() - 1);
        }
    }

    private Tariff getTariff(UUID id) {
        tariffValidator.validateTariffById(id);
        return tariffRepository.findById(id).get();
    }
}
