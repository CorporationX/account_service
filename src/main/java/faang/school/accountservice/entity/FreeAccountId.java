package faang.school.accountservice.entity;

import faang.school.accountservice.enums.AccountType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class FreeAccountId {

    @Column(name = "acc_number", nullable = false)
    private Long accountNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "acc_type", nullable = false, length = 32)
    private AccountType type;
}
