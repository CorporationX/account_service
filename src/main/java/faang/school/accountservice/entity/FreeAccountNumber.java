package faang.school.accountservice.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "free_account_numbers")
@Data
@NoArgsConstructor
public class FreeAccountNumber {

    @EmbeddedId
    private FreeAccountId id;
}
