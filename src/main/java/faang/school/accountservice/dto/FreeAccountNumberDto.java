package faang.school.accountservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FreeAccountNumberDto {
    private long id;
    private long accountNumber;
    private String accountType;
    private LocalDateTime createdAt;
}
