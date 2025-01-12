package faang.school.accountservice.publisher.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountCreateEvent {
    private Long accountId;
    private Long ownerId;
    private LocalDateTime createdAt;
}
