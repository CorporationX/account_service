package faang.school.accountservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "balance_audit")
public class BalanceAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "auth_payment_id", nullable = false)
    private AuthPayment authPayment;



    //TODO AuthPayment Тип операции? Enum?
    //TODO Результат изменения, если authpayment успешно

    @Column(name = "current_auth_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal currentAuthAmount;

    @Column(name = "previous_auth_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal previousAuthAmount;

    @Column(name = "current_fact_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal currentFactAmount;

    @Column(name = "previous_fact_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal previousFactAmount;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "audited_at", nullable = false)
    private LocalDateTime auditedAt;
}
