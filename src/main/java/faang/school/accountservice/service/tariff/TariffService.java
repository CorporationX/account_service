package faang.school.accountservice.service.tariff;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.tariff.TariffRequestDto;
import faang.school.accountservice.dto.tariff.TariffResponseDto;
import faang.school.accountservice.mapper.tariff.TariffMapper;
import faang.school.accountservice.model.interest_rate.InterestRate;
import faang.school.accountservice.model.tariff.Tariff;
import faang.school.accountservice.model.tariff.TariffChangeRecord;
import faang.school.accountservice.repository.TariffRepository;
import faang.school.accountservice.service.interest_rate.InterestRateService;
import faang.school.accountservice.validator.tariff.TariffValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TariffService {

    private final InterestRateService interestRateService;
    private final TariffRepository tariffRepository;
    private final TariffMapper tariffMapper;
    private final TariffValidator tariffValidator;
    private final ObjectMapper objectMapper;
    private static final String CREATING_NEW_TARIFF_ACTION = "CREATE";
    private static final String UPDATING_TARIFF_ACTION = "UPDATE";

    public TariffResponseDto create(TariffRequestDto tariffRequestDto, long userId) {
        Tariff tariff = tariffMapper.toEntity(tariffRequestDto);

        List<TariffChangeRecord> history = new ArrayList<>();
        addToTariffChangeRecords(history,
                new TariffChangeRecord(userId, Instant.now(),
                        CREATING_NEW_TARIFF_ACTION, null,
                        tariffRequestDto.getInterestRateId()),
                tariff);

        tariff = tariffRepository.save(tariff);
        log.info("Tariff created: {}", tariff);
        return tariffMapper.toDto(tariff);
    }

    public TariffResponseDto update(Long tariffId, TariffRequestDto tariffRequestDto, long userId) {
        Tariff tariff = tariffValidator.validateTariffExists(tariffId);

        Long oldInterestRateId = tariff.getInterestRate().getId();
        Long newInterestRateId = tariffRequestDto.getInterestRateId();
        InterestRate interestRateToSet = interestRateService.getInterestRateEntity(newInterestRateId);
        tariff.setInterestRate(interestRateToSet);

        List<TariffChangeRecord> history = getTariffChangeRecords(tariff);
        addToTariffChangeRecords(history,
                new TariffChangeRecord(userId, Instant.now(),
                        UPDATING_TARIFF_ACTION, oldInterestRateId, newInterestRateId),
                tariff);

        tariff = tariffRepository.save(tariff);
        log.info("Tariff updated: {}", tariff);
        return tariffMapper.toDto(tariff);
    }

    public TariffResponseDto get(Long TariffId) {
        log.info("Retrieving Tariff: {}", TariffId);
        Tariff tariff = tariffValidator.validateTariffExists(TariffId);
        return tariffMapper.toDto(tariff);
    }

    public List<TariffResponseDto> getAll() {
        log.info("Retrieving all tariffs");
        List<Tariff> tariffs = tariffRepository.findAll();
        return tariffMapper.toDtos(tariffs);
    }

    public Tariff getTariffEntity(Long TariffId) {
        Tariff tariff = tariffValidator.validateTariffExists(TariffId);
        log.info("Retrieving TariffEntity: {}", TariffId);
        return tariff;
    }

    public void delete(Long tariffId) {
        tariffValidator.validateTariffExists(tariffId);
        log.info("Deleting Tariff: {}", tariffId);
        tariffRepository.deleteById(tariffId);
    }

    public List<TariffChangeRecord> getTariffChangeRecords(Long tariffId) {
        Tariff tariff = tariffValidator.validateTariffExists(tariffId);
        log.info("Retrieving TariffChangeRecords for tariff with id: {}", tariffId);
        return getTariffChangeRecords(tariff);
    }

    private void addToTariffChangeRecords(
            List<TariffChangeRecord> history,
            TariffChangeRecord userId,
            Tariff tariff) {
        history.add(userId);
        try {
            String jsonHistory = objectMapper.writeValueAsString(history);
            tariff.setChangedByUserHistory(jsonHistory);
        } catch (JsonProcessingException e) {
            log.error("Error serializing TariffChangeHistory", e);
            throw new RuntimeException("Json processing error", e);
        }
    }

    private List<TariffChangeRecord> getTariffChangeRecords(Tariff tariff) {
        List<TariffChangeRecord> history = new ArrayList<>();
        try {
            if (tariff.getChangedByUserHistory() != null) {
                history = objectMapper.readValue(tariff.getChangedByUserHistory(), new TypeReference<>() {
                });
            }
        } catch (JsonProcessingException e) {
            log.error("Error deserializing TariffChangeHistory", e);
            throw new RuntimeException("Json processing error", e);
        }
        return history;
    }
}