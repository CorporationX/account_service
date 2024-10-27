package faang.school.accountservice.entity.auth.payment;

import faang.school.accountservice.entity.balance.Balance;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.util.UUID;

@UtilityClass
public class AuthPaymentBuilder {
    public static AuthPayment build(UUID id, Balance balance, BigDecimal amount) {
        return AuthPayment.builder()
                .id(id)
                .balance(balance)
                .amount(amount)
                .build();
    }
}
