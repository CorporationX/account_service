package faang.school.accountservice.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "free_account_numbers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FreeAccountNumber {

    @EmbeddedId
    private FreeAccountNumberId accountId;
}
