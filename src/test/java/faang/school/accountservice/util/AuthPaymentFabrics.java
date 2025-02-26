package faang.school.accountservice.util;

import faang.school.accountservice.model.AuthPayment;
import faang.school.accountservice.model.AuthPaymentStatus;
import faang.school.accountservice.model.Balance;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.util.UUID;

@UtilityClass
public class AuthPaymentFabrics {
    public static AuthPayment buildAuthPayment(UUID id, double amount, Balance balance) {
        return AuthPayment.builder()
                .id(id)
                .amount(BigDecimal.valueOf(amount))
                .balance(balance)
                .build();
    }

    public static AuthPayment buildAuthPayment(UUID id) {
        return AuthPayment.builder()
                .id(id)
                .build();
    }

    public static AuthPayment buildAuthPayment(UUID id, double amount, AuthPaymentStatus status) {
        return AuthPayment.builder()
                .id(id)
                .amount(BigDecimal.valueOf(amount))
                .status(status)
                .build();
    }

    public static AuthPayment buildAuthPayment(AuthPaymentStatus status) {
        return AuthPayment.builder()
                .status(status)
                .build();
    }
}