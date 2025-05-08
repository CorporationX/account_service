package faang.school.accountservice.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "free_account_numbers")
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
public class FreeAccountNumber {

    @EmbeddedId
    private FreeAccountNumberId accountId;
}
