package faang.school.accountservice.dto.request;

import faang.school.accountservice.enums.RequestType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestDto {
    private UUID idempotencyToken;
    private Long userId;
    private RequestType type;

}
