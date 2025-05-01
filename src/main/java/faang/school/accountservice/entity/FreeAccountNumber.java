package faang.school.accountservice.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "free_account_numbers")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FreeAccountNumber {
    @EmbeddedId
    private Key key;

    private Instant createdAt;

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Key implements Serializable {
        private String accountType;
        private String accountNumber;
    }
}
