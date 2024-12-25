package faang.school.accountservice.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "free_acconut_numbers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FreeAccountNumber {

    @EmbeddedId
    private FreeAccountId freeAccountId;

}
