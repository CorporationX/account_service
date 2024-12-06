package faang.school.accountservice.dto.account;

import faang.school.accountservice.entity.request.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RequestDto {

    private AccountCreateDto accountCreateDto;

    private UUID id;

    private String context;

    private RequestStatus requestStatus;

    private LocalDateTime scheduledAt;

}
