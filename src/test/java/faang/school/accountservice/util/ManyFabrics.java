package faang.school.accountservice.util;

import faang.school.accountservice.dto.Money;
import faang.school.accountservice.enums.Currency;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;

@UtilityClass
public class ManyFabrics {
    public static Money buildMoney(Double amount) {
        return new Money(BigDecimal.valueOf(amount), Currency.USD);
    }
}
