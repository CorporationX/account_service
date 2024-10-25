package faang.school.accountservice.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.springframework.data.relational.core.mapping.Table;

@Entity
@Table(name = "savings_account")
public class SavingsAccount {

    @Id
    private Long id;
}
