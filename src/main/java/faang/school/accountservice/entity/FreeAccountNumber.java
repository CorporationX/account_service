package faang.school.accountservice.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "free_account_numbers")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FreeAccountNumber {

    @EmbeddedId
    private FreeAccountId freeAccountId;
}
