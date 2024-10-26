package faang.school.accountservice.entity.auth.payment;

import faang.school.accountservice.dto.Money;
import faang.school.accountservice.entity.balance.Balance;
import lombok.experimental.UtilityClass;

import java.util.UUID;

@UtilityClass
public class AuthPaymentBuilder {
    public static AuthPayment build(Balance balance, Money money) {
        return AuthPayment.builder()
                .id(UUID.randomUUID())
                .balance(balance)
                .amount(money.amount())
                .build();
    }
}
