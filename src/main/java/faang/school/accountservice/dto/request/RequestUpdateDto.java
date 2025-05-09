package faang.school.accountservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestUpdateDto {
    private UUID idempotencyToken;
    private RequestStatusDto requestStatus;
    private Boolean isOpen;
    private String newStatusDetails;
}
