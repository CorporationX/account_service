package faang.school.accountservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class FreeAccountId {
    @Column(name = "type", nullable = false, length = 32)
    @Enumerated(value = EnumType.STRING)
    private AccountType type;

    @Column(name = "account_number", nullable = false)
    private long accountNumber;
}
