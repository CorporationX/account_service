package faang.school.accountservice.dto.Request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestEventPub {
    private Long requestId;
    private Long userId;
    private String status;
    private String message;
}
