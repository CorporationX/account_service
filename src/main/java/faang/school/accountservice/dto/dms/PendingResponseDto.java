package faang.school.accountservice.dto.dms;

import faang.school.accountservice.enums.RequestStatus;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PendingResponseDto{
        private RequestStatus requestStatus;
        private String reason;
        private String operationId;
}
