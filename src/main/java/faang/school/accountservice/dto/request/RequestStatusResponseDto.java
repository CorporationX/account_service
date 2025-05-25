package faang.school.accountservice.dto.request;

import faang.school.accountservice.enums.request.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestStatusResponseDto {
    private UUID requestId;
    private RequestStatus status;
}
