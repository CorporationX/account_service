package faang.school.accountservice.dto.dms;

import faang.school.accountservice.enums.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestOpenDto {
    private RequestStatus status;
    private String operationId;
    private String reason;
}
