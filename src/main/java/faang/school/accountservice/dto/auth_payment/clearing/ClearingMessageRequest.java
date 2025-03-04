package faang.school.accountservice.dto.auth_payment.clearing;

import faang.school.accountservice.enums.auth_payment.clearing.ClearingType;

import java.util.UUID;

public record ClearingMessageRequest(
        UUID paymentId,
        ClearingType clearingType) {}
