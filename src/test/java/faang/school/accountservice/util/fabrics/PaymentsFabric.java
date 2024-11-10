package faang.school.accountservice.util.fabrics;

import faang.school.accountservice.dto.payment.request.AuthPaymentRequest;
import faang.school.accountservice.dto.payment.request.CancelPaymentRequest;
import faang.school.accountservice.dto.payment.request.ClearingPaymentRequest;
import faang.school.accountservice.dto.payment.request.ErrorPaymentRequest;
import faang.school.accountservice.enums.payment.Currency;
import faang.school.accountservice.enums.payment.Category;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.util.UUID;

@UtilityClass
public class PaymentsFabric {
    public static AuthPaymentRequest buildAuthPaymentRequest() {
        return AuthPaymentRequest.builder()
                .build();
    }

    public static AuthPaymentRequest buildAuthPaymentRequest(UUID operationId) {
        return AuthPaymentRequest.builder()
                .operationId(operationId)
                .build();
    }

    public static AuthPaymentRequest buildAuthPaymentRequest(UUID operationId, UUID sourceAccountId,
                                                             UUID targetAccountId, BigDecimal amount,
                                                             Currency currency, Category category) {
        return AuthPaymentRequest.builder()
                .operationId(operationId)
                .sourceAccountId(sourceAccountId)
                .targetAccountId(targetAccountId)
                .amount(amount)
                .currency(currency)
                .category(category)
                .build();
    }

    public static CancelPaymentRequest buildCancelPaymentRequest() {
        return CancelPaymentRequest.builder()
                .build();
    }

    public static CancelPaymentRequest buildCancelPaymentRequest(UUID operationId) {
        return CancelPaymentRequest.builder()
                .operationId(operationId)
                .build();
    }

    public static ClearingPaymentRequest buildClearingPaymentRequest() {
        return ClearingPaymentRequest.builder()
                .build();
    }

    public static ClearingPaymentRequest buildClearingPaymentRequest(UUID operationId) {
        return ClearingPaymentRequest.builder()
                .operationId(operationId)
                .build();
    }

    public static ErrorPaymentRequest buildErrorPaymentRequest() {
        return ErrorPaymentRequest.builder()
                .build();
    }

    public static ErrorPaymentRequest buildErrorPaymentRequest(UUID operationId) {
        return ErrorPaymentRequest.builder()
                .operationId(operationId)
                .build();
    }
}
