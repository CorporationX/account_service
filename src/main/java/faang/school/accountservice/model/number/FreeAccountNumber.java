package faang.school.accountservice.model.number;

import faang.school.accountservice.enums.AccountNumberType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "free_account_number")
@IdClass(FreeAccountNumberId.class)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class FreeAccountNumber {

    @Id
    @Column(name = "type", nullable = false, length = 128)
    @Enumerated(EnumType.STRING)
    private AccountNumberType type;

    @Id
    @Column(name = "digit_sequence", nullable = false, length = 20)
    private String digitSequence;

    @Version
    @Column(name = "version")
    private long version;

    public FreeAccountNumber(AccountNumberType type, String digitSequence) {
        this.type = type;
        this.digitSequence = digitSequence;
    }
}
