package faang.school.accountservice.dto.rollbeck;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class RollbackTaskCrateBalanceDto {
    private long balanceId;
    private List<Long> balanceAuditIds;
}
