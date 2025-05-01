package faang.school.accountservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

import static faang.school.accountservice.constants.Constants.SEQUENCE_TABLE;

@Entity
@Table(name = SEQUENCE_TABLE)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountNumbersSequence {
    @Id
    private String accountType;

    private long currentValue;

    @Version
    private long version;
}
