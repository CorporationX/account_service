package faang.school.accountservice.dto.deadletter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DeadLetterMessage {
    private String originalTopic;
    private Object originalPayload;
    private String errorMessage;
    private LocalDateTime failedAt;
}