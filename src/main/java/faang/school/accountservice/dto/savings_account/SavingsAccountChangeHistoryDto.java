package faang.school.accountservice.dto.savings_account;

import faang.school.accountservice.model.savings_account.SavingsAccountChangeRecord;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SavingsAccountChangeHistoryDto {
    private List<SavingsAccountChangeRecord> changeHistory;
}