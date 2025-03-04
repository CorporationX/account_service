package faang.school.accountservice.dto.auth_payment.cancel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import faang.school.accountservice.enums.auth_payment.cancel.CancelType;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CancelMessageRequest(
        UUID paymentId,
        CancelType cancelType) {}
