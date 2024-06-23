package faang.school.accountservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigInteger;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "free_account_numbers")
public class FreeAccountNumber {
    @EmbeddedId
    private FreeAccountNumberKey id;

    @Column(name = "account_type", nullable = false, insertable = false, updatable = false)
    private String accountType;

    @Column(name = "account_number", nullable = false, insertable = false, updatable = false)
    private BigInteger accountNumber;


    @Embeddable
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class FreeAccountNumberKey implements Serializable {
        @Column(name = "account_type", nullable = false)
        private String accountType;
        @Column(name = "account_number", nullable = false)
        private BigInteger accountNumber;
    }
}