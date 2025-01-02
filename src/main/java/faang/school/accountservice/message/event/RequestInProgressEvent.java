package faang.school.accountservice.message.event;

import faang.school.accountservice.enums.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class RequestInProgressEvent extends NotificationEvent {
    private Long requestId;
    private RequestStatus requestStatus;
    private String statusNote;
    private LocalDateTime createdAt;
}
