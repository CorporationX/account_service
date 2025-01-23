package faang.school.accountservice.entity.account;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Version;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@Entity
@Table(name = "balance", schema = "account_schema")
@Check(constraints = "authorisation_balance >= 0 AND actual_balance >= 0")
public class Balance {
    public Balance() {
        this.authorisationBalance = BigDecimal.ZERO;
        this.actualBalance = BigDecimal.ZERO;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "authorisation_balance", nullable = false, columnDefinition = "DECIMAL DEFAULT 0")
    private BigDecimal authorisationBalance;

    @Column(name = "actual_balance", nullable = false, columnDefinition = "DECIMAL DEFAULT 0")
    private BigDecimal actualBalance;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;
}
