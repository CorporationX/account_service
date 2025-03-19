package faang.school.accountservice.dto.transfer_request;

import faang.school.accountservice.enums.transfer_request.TransferStatus;

import java.util.UUID;

public record TransferResponse(UUID paymentId, TransferStatus result) {}
