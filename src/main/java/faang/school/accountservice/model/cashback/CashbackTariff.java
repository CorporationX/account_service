package faang.school.accountservice.model.cashback;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "cashback_tariff")
public class CashbackTariff {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "cashbackTariff")
    private List<OperationCashback> operationCashback;

    @OneToMany(mappedBy = "cashbackTariff")
    private List<MerchantCashback> merchantCashback;

    @ManyToMany(mappedBy = "cashbackTariffs")
    private List<Merchant> merchants;

    @ManyToMany(mappedBy = "cashbackTariffs")
    private List<OperationType> operationTypes;
}