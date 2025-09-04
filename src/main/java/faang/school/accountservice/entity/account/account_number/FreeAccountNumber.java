package faang.school.accountservice.entity.account.account_number;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "free_account_numbers")
public class FreeAccountNumber {

    @EmbeddedId
    private FreeAccountNumberId id;
}
