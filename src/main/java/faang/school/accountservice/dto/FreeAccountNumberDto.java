package faang.school.accountservice.dto;

import faang.school.accountservice.enums.AccountType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FreeAccountNumberDto {

    private long number;

    @Enumerated(EnumType.STRING)
    private AccountType type;
}
