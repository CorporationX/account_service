package faang.school.accountservice.dto.transfer_request;

import faang.school.accountservice.enums.transfer_request.ClearingType;

import java.util.UUID;

public record ClearingMessageRequest(
        UUID paymentId,
        ClearingType clearingType) {}
