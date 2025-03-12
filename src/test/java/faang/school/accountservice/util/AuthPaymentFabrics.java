package faang.school.accountservice.util;

import faang.school.accountservice.entity.AuthPayment;
import faang.school.accountservice.entity.AuthPaymentStatus;
import faang.school.accountservice.entity.Balance;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.util.UUID;

@UtilityClass
public class AuthPaymentFabrics {
    public static AuthPayment buildAuthPayment(UUID AUTH_PAYMENT_ID, Balance balance, double amount) {

        return AuthPayment.builder()
                .id(AUTH_PAYMENT_ID)
                .balance(balance)
                .amount(BigDecimal.valueOf(amount))
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