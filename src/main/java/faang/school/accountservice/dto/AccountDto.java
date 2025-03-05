package faang.school.accountservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AccountDto {
    private Long id;
    @NotNull
    @Size(min = 12, max = 20)
    private String number;
    @NotNull
    private Boolean projectAccount;
    @NotNull
    private Long owner;
    @NotNull
    private String type;
    @NotNull
    private String currency;
    @NotNull
    private String status;
    private LocalDateTime createdDate;
    private LocalDateTime updateDate;
    private LocalDateTime closeDate;
}
