package faang.school.accountservice.Entity;

import faang.school.accountservice.model.AccTypeFreeNumberId;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "free_accounts_numbers")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FreeAccountNumber {
    @EmbeddedId
    private AccTypeFreeNumberId id;
}
