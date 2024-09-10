package faang.school.accountservice.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "free_account_number")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FreeAccountNumber {

    @EmbeddedId
    private FreeAccountNumberId id;

}
