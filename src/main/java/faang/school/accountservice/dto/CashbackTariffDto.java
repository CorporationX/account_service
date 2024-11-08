package faang.school.accountservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CashbackTariffDto {
    private Long id;
    private String name;
    private LocalDateTime createdAt;
}
