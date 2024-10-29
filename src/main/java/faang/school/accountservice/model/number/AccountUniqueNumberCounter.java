package faang.school.accountservice.model.number;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
}
