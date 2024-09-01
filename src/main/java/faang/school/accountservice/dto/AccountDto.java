package faang.school.accountservice.dto;

import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {
    private Long id;
    private String number;
    private Long userId;
    private Long projectId;
    private String type;
    private Currency currency;
    private Status status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime closedAt;
    private int version;
}