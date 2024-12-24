package faang.school.accountservice.service.interest_rate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.interest_rate.InterestRateDto;
import faang.school.accountservice.mapper.interest_rate.InterestRateMapper;
import faang.school.accountservice.model.interest_rate.InterestRate;
import faang.school.accountservice.model.interest_rate.InterestRateChangeRecord;
import faang.school.accountservice.repository.InterestRateRepository;
import faang.school.accountservice.validator.interest_rate.InterestRateValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InterestRateService {

    private final InterestRateRepository interestRateRepository;
    private final InterestRateValidator interestRateValidator;
    private final InterestRateMapper interestRateMapper;
    private final ObjectMapper objectMapper;
    private static final String CREATING_NEW_INTEREST_RATE_ACTION = "CREATE";
    private static final String UPDATING_INTEREST_RATE_ACTION = "UPDATE";

    public InterestRateDto create(InterestRateDto requestDto, long userId) {
        requestDto.setId(null);
        InterestRate interestRate = interestRateMapper.toEntity(requestDto);

        List<InterestRateChangeRecord> history = new ArrayList<>();
        addToInterestRateChangeRecords(history,
                new InterestRateChangeRecord(userId, Instant.now(),
                        CREATING_NEW_INTEREST_RATE_ACTION, null,
                        requestDto.getInterestRate()),
                interestRate);

        interestRate = interestRateRepository.save(interestRate);
        log.info("Created interest rate {}", interestRate);
        return interestRateMapper.toDto(interestRate);
    }

    public InterestRateDto update(Long interestRateId, InterestRateDto requestDto, long userId) {
        InterestRate interestRate = interestRateValidator.validateInterestRateExists(interestRateId);

        BigDecimal oldValue = interestRate.getInterestRate();
        BigDecimal newValue = requestDto.getInterestRate();
        interestRate.setInterestRate(newValue);

        List<InterestRateChangeRecord> history = getInterestRateChangeRecords(interestRate);
        addToInterestRateChangeRecords(history,
                new InterestRateChangeRecord(userId, Instant.now(),
                        UPDATING_INTEREST_RATE_ACTION, oldValue, newValue),
                interestRate);

        interestRate = interestRateRepository.save(interestRate);
        log.info("Updated interest rate {}", interestRate);
        return interestRateMapper.toDto(interestRate);
    }

    public InterestRateDto get(Long interestRateId) {
        log.info("Retrieving interest rate {}", interestRateId);
        InterestRate interestRate = interestRateValidator.validateInterestRateExists(interestRateId);
        return interestRateMapper.toDto(interestRate);
    }

    public List<InterestRateDto> getAll() {
        log.info("Retrieving all interest rates");
        List<InterestRate> interestRates = interestRateRepository.findAll();
        return interestRateMapper.toListDto(interestRates);
    }

    public InterestRate getInterestRateEntity(Long interestRateId) {
        InterestRate interestRate = interestRateValidator.validateInterestRateExists(interestRateId);
        log.info("Retrieving InterestRateEntity by id {}", interestRateId);
        return interestRate;
    }

    public void delete(Long interestRateId) {
        interestRateValidator.validateInterestRateExists(interestRateId);
        log.info("Deleting interest rate {}", interestRateId);
        interestRateRepository.deleteById(interestRateId);
    }

    public List<InterestRateChangeRecord> getInterestRateChangeRecords(Long interestRateId) {
        InterestRate interestRate = interestRateValidator.validateInterestRateExists(interestRateId);
        log.info("Getting interest rate change records for the InterestRate with id: {} ", interestRateId);
        return getInterestRateChangeRecords(interestRate);
    }

    private void addToInterestRateChangeRecords(
            List<InterestRateChangeRecord> history,
            InterestRateChangeRecord userId,
            InterestRate interestRate) {
        history.add(userId);
        try {
            String jsonHistory = objectMapper.writeValueAsString(history);
            interestRate.setChangedByUserHistory(jsonHistory);
        } catch (JsonProcessingException e) {
            log.error("Error serializing InterestRateChangeHistory", e);
            throw new RuntimeException("Json processing error", e);
        }
    }

    private List<InterestRateChangeRecord> getInterestRateChangeRecords(
            InterestRate interestRate) {
        List<InterestRateChangeRecord> history = new ArrayList<>();
        try {
            if (interestRate.getChangedByUserHistory() != null) {
                history = objectMapper.readValue(interestRate.getChangedByUserHistory(), new TypeReference<>() {
                });
            }
        } catch (JsonProcessingException e) {
            log.error("Error deserializing InterestRateChangeHistory", e);
            throw new RuntimeException("Json processing error", e);
        }
        return history;
    }
}