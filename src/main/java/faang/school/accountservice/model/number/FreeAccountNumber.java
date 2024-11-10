package faang.school.accountservice.model.number;

import faang.school.accountservice.enums.AccountNumberType;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "free_account_number")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class FreeAccountNumber {

    @EmbeddedId
    private FreeAccountNumberId id;

    @Version
    @Column(name = "version")
    private long version;

    public FreeAccountNumber(AccountNumberType type, String digitSequence) {
        this.id = new FreeAccountNumberId(type, digitSequence);
    }

    public String getDigitSequence() {
        return id.getDigitSequence();
    }

    public AccountNumberType getType() {
        return id.getType();
    }
}
