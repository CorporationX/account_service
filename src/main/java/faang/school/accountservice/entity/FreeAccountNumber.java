package faang.school.accountservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "free_account_numbers")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class FreeAccountNumber {

    @EmbeddedId
    private FreeAccountNumberId id;

    @Version
    @Column(name = "version")
    private Long version;

    public FreeAccountNumber(FreeAccountNumberId id) {
        this.id = id;
    }
}
