package faang.school.accountservice.dto.account.saving;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavingAccountFilter {
    private Long userId;
    private Long projectId;
}
