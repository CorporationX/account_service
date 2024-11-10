package faang.school.accountservice.util.fabrics;

import faang.school.accountservice.dto.Money;
import faang.school.accountservice.enums.payment.Currency;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;

@UtilityClass
public class MoneyFabric {
    public static Money buildMoney(double amount) {
        return new Money(BigDecimal.valueOf(amount), Currency.USD);
    }
}
