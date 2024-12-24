package faang.school.accountservice.dto.interest_rate;

import faang.school.accountservice.model.interest_rate.InterestRateChangeRecord;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InterestRateChangeHistoryDto {
    private List<InterestRateChangeRecord> changeHistory;
}