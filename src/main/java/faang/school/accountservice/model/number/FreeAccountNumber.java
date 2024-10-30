package faang.school.accountservice.model.number;

import faang.school.accountservice.enums.AccountNumberType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "free_account_number")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FreeAccountNumber {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type", nullable = false, length = 128)
    @Enumerated(EnumType.STRING)
    private AccountNumberType type;

    @Column(name = "account_number", nullable = false, length = 20)
    private String digitSequence;

    @Version
    @Column(name = "version")
    private long version;

    public FreeAccountNumber(AccountNumberType type, String accountNumber) {
        this.type = type;
        this.digitSequence = accountNumber;
    }
}
