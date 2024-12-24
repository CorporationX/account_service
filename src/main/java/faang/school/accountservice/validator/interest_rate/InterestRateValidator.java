package faang.school.accountservice.validator.interest_rate;

import faang.school.accountservice.dto.interest_rate.InterestRateDto;
import faang.school.accountservice.exception.interest_rate.InterestRateNotFound;
import faang.school.accountservice.exception.interest_rate.MaximumAllowedInterestRateException;
import faang.school.accountservice.model.interest_rate.InterestRate;
import faang.school.accountservice.repository.InterestRateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class InterestRateValidator {

    @Value("${maximum_allowed_interest_rate}")
    private BigDecimal maxAllowedInterestRate;
    private final InterestRateRepository interestRateRepository;

    public void validateInterestRateDoesNotExceedMax(InterestRateDto requestDto) {
        if (requestDto.getInterestRate().compareTo(maxAllowedInterestRate) > 0) {
            log.error("Maximum allowed interest rate exceeded");
            throw new MaximumAllowedInterestRateException("Interest rate cannot exceed the maximum allowed value");
        }
    }

    public InterestRate validateInterestRateExists(Long id) {
        Optional<InterestRate> interestRate = interestRateRepository.findById(id);
        if (interestRate.isEmpty()) {
            log.error("Interest rate with id: {} does not exist", id);
            throw new InterestRateNotFound("Interest rate with id: " + id + " does not exist");
        }
        return interestRate.get();
    }
}