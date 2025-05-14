package faang.school.accountservice.entity;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "free_account_numbers")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FreeAccountNumber {
    @EmbeddedId
    @AttributeOverrides({
            @AttributeOverride(name = "accountType",
                    column = @Column(name = "account_type", nullable = false, length = 50)),
            @AttributeOverride(name = "accountNumber",
                    column = @Column(name = "account_number", nullable = false, length = 20))
    })
    private Key key;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Key implements Serializable {
        private static final long serialVersionUID = 1L;

        private String accountType;
        private String accountNumber;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Key)) return false;
            Key that = (Key) o;
            return Objects.equals(accountType, that.accountType)
                    && Objects.equals(accountNumber, that.accountNumber);
        }

        @Override
        public int hashCode() {
            return Objects.hash(accountType, accountNumber);
        }
    }
}
