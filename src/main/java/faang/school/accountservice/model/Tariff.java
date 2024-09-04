package faang.school.accountservice.model;

import faang.school.accountservice.enums.TarifType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "tariff")
public class Tariff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "tariff_type")
    private TarifType tariffType;

    @Column(name = "stakes")
    private String stakes;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "savings_account_id", referencedColumnName = "id")
    private SavingsAccount savingsAccount;
}
