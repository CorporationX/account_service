package faang.school.accountservice.entity;

import faang.school.accountservice.enums.AccountType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Embeddable
public class FreeAccountId implements Serializable {

    @Column(name = "type", nullable = false, length = 32)
    @Enumerated(EnumType.STRING)
    private AccountType type;

    @Column(name = "account_number", nullable = false)
    private Long accountNumber;
}
