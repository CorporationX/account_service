package faang.school.accountservice.model.account;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "free_account_numbers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FreeAccountNumber {

    @EmbeddedId
    private FreeAccountId id;

}
