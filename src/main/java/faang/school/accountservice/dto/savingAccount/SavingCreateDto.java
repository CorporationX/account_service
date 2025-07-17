package faang.school.accountservice.dto.savingAccount;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SavingCreateDto {
    private UUID accountId;
    private TariffSavingType savingType;
}
