package faang.school.accountservice.model;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "cashback_merchant_mapping")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CashbackMerchantMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tariff_id", nullable = false)
    private Long tariffId;

    @Column(name = "merchant", nullable = false)
    private String merchant;

    @Column(name = "cashback_percent", nullable = false)
    private BigDecimal cashbackPercent;

}