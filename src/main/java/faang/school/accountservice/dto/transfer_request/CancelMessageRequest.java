package faang.school.accountservice.dto.transfer_request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import faang.school.accountservice.enums.transfer_request.CancelType;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CancelMessageRequest(
        UUID paymentId,
        CancelType cancelType) {}
