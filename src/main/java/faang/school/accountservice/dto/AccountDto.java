package faang.school.accountservice.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class AccountDto extends AccountPreviewDto{
    private String accountNumber;
    private AccountBalanceDto balance;
    private LocalDateTime updatedAt;
}
