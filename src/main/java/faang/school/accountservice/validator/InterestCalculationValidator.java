package faang.school.accountservice.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.Period;

@Component
@RequiredArgsConstructor
public class InterestCalculationValidator {

    @Value("${interest.calculation.period:P1D}")
    private String calculationPeriod;

    public boolean validateLastInterestCalculationDate(LocalDateTime lastCalculationDate) {
        if (lastCalculationDate == null) {
            return true;
        }

        Period period = Period.parse(calculationPeriod);

        LocalDateTime nextCalculationDate = lastCalculationDate.plus(period);
        return LocalDateTime.now().isAfter(nextCalculationDate);
    }
}
