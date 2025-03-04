package faang.school.accountservice.dto.auth_payment;

import faang.school.accountservice.enums.auth_payment.PaymentProcessingResult;

import java.util.UUID;

public record PaymentResponse(UUID paymentId, PaymentProcessingResult result) {}
