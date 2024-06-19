package faang.school.accountservice.validator;

import faang.school.accountservice.exception.DataValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
public class AccountValidator {

    public void validateBalance(String number, BigDecimal summa, BigDecimal balance) {
        if (balance.compareTo(summa) > -1) {
            String errMessage = String.format("There are not enough funds on account %s for debiting", number);
            log.error(errMessage, number);
            throw new DataValidationException(errMessage);
        }
    }
}
