package faang.school.accountservice.dto.event.responce.failed;

import faang.school.accountservice.enums.TransferStage;
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
public class ClearFailedEvent {
    private UUID transactionId;
    private String description;
    @Builder.Default
    private TransferStage transferStage = TransferStage.CLEAR_FAILED;
}