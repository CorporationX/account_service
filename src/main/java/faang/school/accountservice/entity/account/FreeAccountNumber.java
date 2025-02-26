package faang.school.accountservice.entity.account;

import faang.school.accountservice.enums.AccountType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "free_account_numbers")
public class FreeAccountNumber {

    @EmbeddedId
    private FreeAccountNumberId id;

    @Data
    @Embeddable
    public static class FreeAccountNumberId implements Serializable {

        @Column(nullable = false, length = 20)
        private AccountType type;

        @Column(name = "account_number", nullable = false, length = 20)
        private String accountNumber;
    }

}
