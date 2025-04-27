package faang.school.accountservice.service.tariff;

import faang.school.accountservice.dto.tariff.TariffCreationDto;
import faang.school.accountservice.dto.tariff.TariffResponseDto;
import faang.school.accountservice.entity.Tariff;
import faang.school.accountservice.exception.TariffNotFoundException;
import faang.school.accountservice.mapper.TariffMapper;
import faang.school.accountservice.repository.TariffRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TariffServiceImpl implements TariffService {
    private final TariffRepository tariffRepository;
    private final TariffMapper tariffMapper;

    @Override
    @Transactional
    public TariffResponseDto createTariff(TariffCreationDto dto) {
        log.info("Creating new tariff: {}", dto);
        Tariff tariff = tariffMapper.toTariff(dto);
        tariffRepository.save(tariff);
        log.info("Tariff successfully created with id: {}", tariff.getId());
        return tariffMapper.toTariffResponseDto(tariff);
    }

    @Override
    @Transactional
    public TariffResponseDto updateTariffRate(Long tariffId, BigDecimal newRate) {
        log.info("Updating tariff with id {} with new rate: {}", tariffId, newRate);
        Tariff tariff = findTariffByIdInRepo(tariffId);
        validateNewRate(newRate, tariffId);
        tariff.addRate(newRate);
        Tariff savedTariff = tariffRepository.save(tariff);
        log.info("Tariff with id {} successfully updated", tariffId);
        return tariffMapper.toTariffResponseDto(savedTariff);
    }

    @Override
    public TariffResponseDto getTariff(Long tariffId) {
        log.info("Retrieving tariff with id: {}", tariffId);
        Tariff tariff = findTariffByIdInRepo(tariffId);
        log.info("Tariff with id {} was successfully retrieved", tariffId);
        return tariffMapper.toTariffResponseDto(tariff);
    }

    @Override
    public List<TariffResponseDto> getAllTariffs() {
        log.info("Retrieving all tariffs");
        List<Tariff> tariffs = tariffRepository.findAll();
        log.info("Total tariffs found: {}", tariffs.size());
        return tariffMapper.toTariffResponseDtoList(tariffs);
    }

    public boolean existTariff

    private void validateNewRate(BigDecimal newRate, Long tariffId) {
        if (newRate == null) {
            log.error("New rate is null for tariff with id: {}", tariffId);
            throw new IllegalArgumentException("Rate value cannot be null");
        }

        if (newRate.compareTo(BigDecimal.ZERO) <= 0) {
            log.error("Invalid new rate value: {} for tariff with id: {}", newRate, tariffId);
            throw new IllegalArgumentException("Rate value cannot be negative");
        }
    }

    private Tariff findTariffByIdInRepo(Long tariffId) {
        return tariffRepository.findById(tariffId)
                .orElseThrow(() -> {
                    log.error("Tariff with id {} not found", tariffId);
                    return new TariffNotFoundException("Tariff not found");
                });
    }
}
