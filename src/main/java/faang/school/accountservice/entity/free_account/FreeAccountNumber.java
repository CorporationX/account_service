package faang.school.accountservice.entity.free_account;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "free_account_number")
@Data
@NoArgsConstructor
public class FreeAccountNumber {

    @EmbeddedId
    private FreeAccountNumberId freeAccountNumberId;
}
