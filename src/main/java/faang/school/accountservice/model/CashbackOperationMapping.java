package faang.school.accountservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "cashback_operation_mapping")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CashbackOperationMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tariff_id", nullable = false)
    private Long tariffId;

    @Column(name = "operation_type", nullable = false)
    private String operationType;

    @Column(name = "cashback_percent", nullable = false)
    private BigDecimal cashbackPercent;

}