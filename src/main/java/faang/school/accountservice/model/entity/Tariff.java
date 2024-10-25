package faang.school.accountservice.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import org.springframework.data.relational.core.mapping.Table;

@Entity
@Table(name = "tariff")
public class Tariff {

    @Id
    @GeneratedValue
    private Long id;

}
