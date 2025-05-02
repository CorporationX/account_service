package faang.school.accountservice.dto.request;

import faang.school.accountservice.enums.RequestStatus;
import faang.school.accountservice.enums.RequestType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestResponseDto {

    private UUID idempotencyKey;
    private Long userId;
    private RequestType requestType;
    private Map<String, Object> inputData;
    private RequestStatus requestStatus;
    private String statusDescription;
}
