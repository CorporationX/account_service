package faang.school.accountservice.dto.tariff;

import faang.school.accountservice.model.tariff.TariffChangeRecord;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TariffChangeHistoryDto {
    private List<TariffChangeRecord> changeHistory;
}