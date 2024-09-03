package faang.school.accountservice.service.account;

import faang.school.accountservice.dto.account.TariffDto;
import faang.school.accountservice.mapper.account.TariffMapper;
import faang.school.accountservice.repository.TariffRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TariffService {

    private final TariffMapper tariffMapper;
    private final TariffRepository tariffRepository;

    public TariffDto createTariff(TariffDto tariffDto) {
        var tariff = tariffMapper.toEntity(tariffDto);
        tariff = tariffRepository.save(tariff);
        return tariffMapper.toDto(tariff);
    }

    public TariffDto updateTariff(Long tariffId, TariffDto tariffDto) {
        var existingTariff = tariffRepository.findById(tariffId)
                .orElseThrow(() -> new EntityNotFoundException("Tariff not found"));
        tariffMapper.update(tariffDto, existingTariff);
        existingTariff = tariffRepository.save(existingTariff);
        return tariffMapper.toDto(existingTariff);
    }

    public List<TariffDto> getAllTariffs() {
        var all = tariffRepository.findAll();
        return all.stream()
                .map(tariffMapper::toDto)
                .toList();
    }
}
