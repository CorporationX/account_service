package faang.school.accountservice.entity.cashback;

import faang.school.accountservice.entity.BaseEntity;
import faang.school.accountservice.entity.Merchant;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "cashback_rules")
public class CashbackRule extends BaseEntity {

    @Column(name = "transaction_type", length = 32)
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id")
    private Merchant merchant;

    @Column(name = "percentage", nullable = false)
    private Integer percentage;

    @ManyToMany(mappedBy = "rules")
    private List<CashbackPlan> cashbackPlans;
}
