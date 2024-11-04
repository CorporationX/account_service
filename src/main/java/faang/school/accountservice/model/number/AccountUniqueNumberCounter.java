package faang.school.accountservice.model.number;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "account_unique_number_counter")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AccountUniqueNumberCounter {

    @Id
    @Column(name = "type")
    private String type;

    @Column(name = "counter")
    private Long counter;

    @Column(name = "generation_state")
    private Boolean generation_state;
}
