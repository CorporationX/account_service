package faang.school.accountservice.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class InterestCalculationValidator {

    public boolean validateLastInterestCalculationDate(LocalDateTime date) {
        if (date == null || LocalDateTime.now().isAfter(date.plusDays(1)) ) {
            return true;
        }
        return false;
    }
}
