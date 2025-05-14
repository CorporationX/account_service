package faang.school.accountservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Entity
@Table(name = "account_numbers_sequence")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountNumbersSequence {
    @Id
    @Column(name = "account_type", nullable = false, length = 50)
    private String accountType;

    @Column(name = "current_value", nullable = false)
    private long currentValue;

    @Version
    @Column(name = "version", nullable = false)
    private long version;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AccountNumbersSequence)) return false;
        AccountNumbersSequence that = (AccountNumbersSequence) o;
        return Objects.equals(accountType, that.accountType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountType);
    }
}
