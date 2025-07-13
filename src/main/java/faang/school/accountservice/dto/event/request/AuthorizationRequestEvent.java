package faang.school.accountservice.dto.event.request;

import faang.school.accountservice.enums.Category;
import faang.school.accountservice.enums.Currency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthorizationRequestEvent {
    private UUID authorizationId;
    private Long userId;
    private UUID sourceId;
    private UUID targetId;
    private BigDecimal amount;
    private Currency currency;
    private Category category;
}