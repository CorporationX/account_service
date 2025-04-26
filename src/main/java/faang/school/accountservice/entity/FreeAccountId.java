package faang.school.accountservice.entity;

import faang.school.accountservice.enums.AccountType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.Objects;

@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class FreeAccountId implements Serializable {
    @Column(name = "type", nullable = false, length = 32)
    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    @Column(name = "account_number", nullable = false)
    private Long accountNumber;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FreeAccountId that = (FreeAccountId) o;
        return accountType == that.accountType && Objects.equals(accountNumber, that.accountNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountType, accountNumber);
    }
}
