package faang.school.accountservice.dto.cashback;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CashbackPlanReadDto {
    private Long id;
    private String description;
    private List<Long> rules;
}
