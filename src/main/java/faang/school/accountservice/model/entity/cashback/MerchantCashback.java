package faang.school.accountservice.model.entity.cashback;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "merchant_cashback")
public class MerchantCashback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id;

    @Column(name = "merchant_id", nullable = false)
    public String merchantId;

    @Builder.Default
    @OneToMany(mappedBy = "merchant")
    private List<CashbackTariffMerchant> cashbackTariffMerchants = new ArrayList<>();
}
