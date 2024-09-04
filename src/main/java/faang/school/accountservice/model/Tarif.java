package faang.school.accountservice.model;

import faang.school.accountservice.enums.TarifType;
import jakarta.persistence.*;

@Entity
@Table(name = "tarif")
public class Tarif {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "type")
    private TarifType type;

    @Column(name = "stakes")
    private String stakes;
}
