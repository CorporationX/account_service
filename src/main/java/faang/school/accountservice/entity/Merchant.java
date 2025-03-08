package faang.school.accountservice.entity;

import faang.school.accountservice.entity.cashback.CashbackRule;
import faang.school.accountservice.entity.cashback.MerchantType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
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
@Table(name = "merchants")
public class Merchant extends BaseEntity {
    @Column(name = "merchant_type", length = 16, nullable = false)
    @Enumerated(EnumType.STRING)
    private MerchantType merchantType;

    @Column(name = "merchant_id", nullable = false)
    private Integer merchantId;

    @OneToMany(mappedBy = "merchant")
    private List<CashbackRule> cashbackRules;
}
