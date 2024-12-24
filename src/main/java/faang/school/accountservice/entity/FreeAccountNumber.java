package faang.school.accountservice.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "free_account_numbers")
@AllArgsConstructor
@NoArgsConstructor
public class FreeAccountNumber {
    @Id
    @EmbeddedId
    private FreeAccountId id;
}
