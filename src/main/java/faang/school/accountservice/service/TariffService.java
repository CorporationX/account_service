package faang.school.accountservice.service;

import faang.school.accountservice.dto.CreateTariffRequest;
import faang.school.accountservice.dto.TariffDto;
import faang.school.accountservice.dto.UpdateTariffRequest;
import faang.school.accountservice.exception.ResourceNotFoundException;
import faang.school.accountservice.mapper.TariffMapper;
import faang.school.accountservice.model.Tariff;
import faang.school.accountservice.model.TariffRateHistory;
import faang.school.accountservice.repository.TariffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TariffService {
    private final TariffRepository tariffRepository;
    private final TariffMapper tariffMapper;

    public TariffDto createTariff(CreateTariffRequest request) {
        Tariff tariff = Tariff.builder()
                .name(request.getName())
                .build();
        tariff = tariffRepository.save(tariff);

        TariffRateHistory initialRateHistory = TariffRateHistory.builder()
                .tariff(tariff)
                .rate(request.getInitialRate())
                .build();
        tariff.getRateHistory().add(initialRateHistory);

        return tariffMapper.toDto(tariff);
    }


    public TariffDto updateTariff(Long id, UpdateTariffRequest request){
        Tariff tariff = getTariff(id);

        TariffRateHistory newRateHistory = TariffRateHistory.builder()
                .tariff(tariff)
                .rate(request.getNewRate())
                .build();
        tariff.getRateHistory().add(newRateHistory);
        tariff = tariffRepository.save(tariff);
        return tariffMapper.toDto(tariff);
    }

    public Tariff getTariff(Long id) {
        return tariffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tariff not found"));
    }

    public TariffDto getTariffById(Long id) {
        Tariff tariff = getTariff(id);
        return tariffMapper.toDto(tariff);
    }
}
