package faang.school.accountservice.model.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "free_account_numbers")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FreeAccountNumber {

    @EmbeddedId
    private FreeAccountNumberId id;
}
