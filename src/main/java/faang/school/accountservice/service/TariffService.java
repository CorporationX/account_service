package faang.school.accountservice.service;

import faang.school.accountservice.dto.tariff.AddRateTariffRequest;
import faang.school.accountservice.dto.tariff.CreateTariffRequest;
import faang.school.accountservice.dto.tariff.TariffDto;
import faang.school.accountservice.mapper.TariffMapper;
import faang.school.accountservice.model.tariff.Tariff;
import faang.school.accountservice.repository.TariffRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@Service
public class TariffService {

    private final TariffRepository tariffRepository;
    private final TariffMapper tariffMapper;

    @Transactional(readOnly = true)
    public Tariff getTariffById(Long tariffId) {
        return tariffRepository.findById(tariffId)
                .orElseThrow(() -> new EntityNotFoundException("Tariff id " + tariffId + " not found"));
    }

    @Transactional(readOnly = true)
    public TariffDto getTariffDtoById(Long tariffId) {
        return tariffMapper.toTariffDto(tariffRepository.findById(tariffId)
                .orElseThrow(() -> new EntityNotFoundException("Tariff id " + tariffId + " not found")));
    }

    @Transactional
    public TariffDto addTariff(CreateTariffRequest createTariffRequest) {
        Tariff tariff = tariffMapper.toEntity(createTariffRequest);
        return tariffMapper.toTariffDto(tariffRepository.save(tariff));
    }

    @Transactional
    public TariffDto addRate(AddRateTariffRequest addRateTariffRequest) {
        Tariff tariff = getTariffById(addRateTariffRequest.id());

        tariff.getRateHistory().add(addRateTariffRequest.rate());
        return tariffMapper.toTariffDto(tariff);
    }

    @Transactional(readOnly = true)
    public BigDecimal getActualRate(@Valid @NotNull @Positive Long tariffId) {
        Tariff tariff = getTariffById(tariffId);
        return tariff.getRateHistory().get(tariff.getRateHistory().size() - 1);
    }

    @Transactional(readOnly = true)
    public List<TariffDto> getTariffsDto() {
        return tariffRepository.findAll().stream()
                .map(tariffMapper::toTariffDto)
                .toList();
    }
}
