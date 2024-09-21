package faang.school.accountservice.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "free_account_numbers")
@AllArgsConstructor
@NoArgsConstructor
public class FreeAccountNumber {
    @EmbeddedId
    private FreeAccountId id;
}
