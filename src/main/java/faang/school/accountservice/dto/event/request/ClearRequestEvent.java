package faang.school.accountservice.dto.event.request;

import faang.school.accountservice.enums.Initiator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClearRequestEvent {
    private UUID transactionId;
    private Initiator initiator;
    private Long userId;
}