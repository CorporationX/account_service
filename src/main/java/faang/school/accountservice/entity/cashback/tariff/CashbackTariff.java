package faang.school.accountservice.entity.cashback.tariff;

import faang.school.accountservice.entity.cashback.MerchantCashback;
import faang.school.accountservice.entity.cashback.OperationCashback;
import faang.school.accountservice.entity.type.Merchant;
import faang.school.accountservice.entity.type.OperationType;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
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

    @ManyToMany
    @JoinTable(
            name = "merchant_cashback",
            joinColumns = @JoinColumn(name = "cashback_tariff_id"),
            inverseJoinColumns = @JoinColumn(name = "merchant_id")
    )
    private List<Merchant> merchants;

    @ManyToMany
    @JoinTable(
            name = "operation_cashback",
            joinColumns = @JoinColumn(name = "cashback_tariff_id"),
            inverseJoinColumns = @JoinColumn(name = "operation_id")
    )
    private List<OperationType> operationTypes;
}
