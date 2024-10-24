package faang.school.accountservice.dto;


import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class SavingsAccountDto {
    private Long accountId;
    private Long tariffId;
    private List<TariffHistoryDto> tariffHistoryDto;
    private Long holderUserId;
    private Long holderUserProjectId;
}
