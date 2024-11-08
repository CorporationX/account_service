package faang.school.accountservice.entity.auth.payment;

import faang.school.accountservice.entity.balance.Balance;
import faang.school.accountservice.enums.pending.Category;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.util.UUID;

@UtilityClass
public class AuthPaymentBuilder {
    public static AuthPayment build(UUID id, Balance sourceBalance, Balance targetBalance,
                                    BigDecimal amount, Category category) {
        return AuthPayment.builder()
                .id(id)
                .sourceBalance(sourceBalance)
                .targetBalance(targetBalance)
                .category(category)
                .amount(amount)
                .build();
    }
}
