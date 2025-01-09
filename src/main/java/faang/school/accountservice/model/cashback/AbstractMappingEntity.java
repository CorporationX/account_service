package faang.school.accountservice.model.cashback;

import jakarta.persistence.*;
import lombok.Data;

@MappedSuperclass
@Data
public abstract class AbstractMappingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(name = "name", nullable = false)
    protected String name;
}