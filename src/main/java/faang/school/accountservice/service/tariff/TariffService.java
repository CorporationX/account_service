package faang.school.accountservice.service.tariff;

import faang.school.accountservice.dto.tariff.TariffCreateDto;
import faang.school.accountservice.dto.tariff.TariffResponse;
import faang.school.accountservice.dto.tariff.TariffUpdateDto;
import faang.school.accountservice.entity.tariff.Tariff;
import faang.school.accountservice.entity.tariff.TariffRateChangelog;
import faang.school.accountservice.exception.UniqueConstraintException;
import faang.school.accountservice.mapper.tariff.TariffMapper;
import faang.school.accountservice.repository.tariff.TariffRateChangelogRepository;
import faang.school.accountservice.repository.tariff.TariffRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TariffService {

    private final TariffRepository tariffRepository;
    private final TariffRateChangelogService tariffRateChangelogService;
    private final TariffMapper tariffMapper;

    @Transactional
    public TariffResponse createTariff(TariffCreateDto creationDto) {
        String tariffName = creationDto.getName();
        BigDecimal tariffRate = creationDto.getRate();
        log.info("Received request to create a new tariff with name '{}' and initial rate={}", tariffName, tariffRate);
        Tariff tariff = new Tariff();
        try {
            tariff.setName(creationDto.getName());
            tariffRepository.save(tariff);
            TariffRateChangelog rateChangelog = buildTariffRateChangelog(tariff, tariffRate);
            tariffRateChangelogService.save(rateChangelog);
            log.info("A new tariff named '{}' with initial rate={} was successfully created.", tariffName, tariffRate);
        } catch (DataIntegrityViolationException ex) {
            handleUniqueConstraintViolation(ex, tariffName);
        }
        return tariffMapper.toResponse(tariff);
    }

    @Transactional
    @Retryable(
            retryFor = OptimisticLockingFailureException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000)
    )
    public TariffResponse updateTariff(long tariffId, TariffUpdateDto updateDto) {
        log.info("Received request to update the tariff with ID={}", tariffId);
        Tariff tariff = getTariff(tariffId);
        if (updateDto.getRate() != null) {
            TariffRateChangelog tariffRateChangelog = buildTariffRateChangelog(tariff, updateDto.getRate());
            tariffRateChangelogService.save(tariffRateChangelog);
        }
        if (updateDto.getName() != null && !updateDto.getName().isBlank()) {
            try {
                tariff.setName(updateDto.getName());
                tariffRepository.save(tariff);
            } catch (DataIntegrityViolationException ex) {
                handleUniqueConstraintViolation(ex, tariff.getName());
            }
        }
        TariffResponse tariffResponse = tariffMapper.toResponse(tariff);
        log.info("The tariff with ID={} was updated: {}", tariffId, tariffResponse);
        return tariffResponse;
    }

    @Transactional(readOnly = true)
    public List<TariffResponse> getAllTariffs() {
        return tariffRepository.findAll().stream()
                .map(tariffMapper::toResponse)
                .toList();
    }

    public void deleteTariff(long tariffId) {
        tariffRepository.deleteById(tariffId);
    }

    private void handleUniqueConstraintViolation(DataIntegrityViolationException ex, String tariffName) {
        if (ex.getMessage().contains("constraint [tariff_name_key]")) {
            String exceptionMessage = "Unable to set tariff name='%s': there is already existing tariff with this name."
                    .formatted(tariffName);
            throw new UniqueConstraintException(exceptionMessage, ex);
        }
    }

    private Tariff getTariff(long tariffId) {
        return tariffRepository.findById(tariffId)
                .orElseThrow(() -> new EntityNotFoundException("There is no tariff with ID=%d found in database.".formatted(tariffId)));
    }

    private TariffRateChangelog buildTariffRateChangelog(Tariff tariff, BigDecimal rate) {
        return TariffRateChangelog
                .builder()
                .tariff(tariff)
                .rate(rate)
                .build();
    }
}