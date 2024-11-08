package faang.school.accountservice.util.fabrics;

import faang.school.accountservice.entity.auth.payment.AuthPayment;
import faang.school.accountservice.enums.auth.payment.AuthPaymentStatus;
import faang.school.accountservice.entity.balance.Balance;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.util.UUID;

@UtilityClass
public class AuthPaymentFabric {
    public static AuthPayment buildAuthPayment() {
        return AuthPayment.builder()
                .build();
    }

    public static AuthPayment buildAuthPayment(UUID id) {
        return AuthPayment.builder()
                .id(id)
                .build();
    }

    public static AuthPayment buildAuthPayment(AuthPaymentStatus status) {
        return AuthPayment.builder()
                .status(status)
                .build();
    }

    public static AuthPayment buildAuthPayment(UUID id, double amount) {
        return AuthPayment.builder()
                .id(id)
                .amount(BigDecimal.valueOf(amount))
                .build();
    }

    public static AuthPayment buildAuthPayment(UUID id, double amount, AuthPaymentStatus status) {
        return AuthPayment.builder()
                .id(id)
                .amount(BigDecimal.valueOf(amount))
                .status(status)
                .build();
    }

    public static AuthPayment buildAuthPayment(UUID id, double amount, Balance balance) {
        return AuthPayment.builder()
                .id(id)
                .amount(BigDecimal.valueOf(amount))
                .balance(balance)
                .build();
    }
}
