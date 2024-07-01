package faang.school.accountservice.service;

import faang.school.accountservice.dto.CreateTariffRequest;
import faang.school.accountservice.dto.TariffDto;
import faang.school.accountservice.dto.UpdateTariffRequest;
import faang.school.accountservice.exception.ResourceNotFoundException;
import faang.school.accountservice.mapper.EntityMapper;
import faang.school.accountservice.model.Tariff;
import faang.school.accountservice.repository.TariffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TariffService {
    private final TariffRepository tariffRepository;
    private final EntityMapper entityMapper;

    public TariffDto createTariff(CreateTariffRequest request){
        Tariff tariff = Tariff.builder()
                .name(request.getName())
                .rateHistory("[" + request.getInitialRate() + "]")
                .build();
        tariffRepository.save(tariff);
        return entityMapper.toDto(tariff);
    }

    public TariffDto updateTariff(Long id, UpdateTariffRequest request){
        Tariff tariff = getTariff(id);
        String newRateHistory = tariff.getRateHistory() + ", " + request.getNewRate();
        tariff.setRateHistory(newRateHistory);
        tariffRepository.save(tariff);
        return entityMapper.toDto(tariff);
    }

    public Tariff getTariff(Long id) {
        return tariffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tariff not found"));
    }

    public TariffDto getTariffById(Long id) {
        Tariff tariff = getTariff(id);
        return entityMapper.toDto(tariff);
    }
}
