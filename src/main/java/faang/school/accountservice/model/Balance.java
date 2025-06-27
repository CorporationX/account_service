package faang.school.accountservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "balance")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Balance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_number", nullable = false, length = 20)
    @Pattern(regexp = "\\d{12,20}", message = "Account number must be 12 to 20 digits")
    private String accountNumber;

    @Column(name = "authorization_balance", nullable = false, precision = 19, scale = 4)
    private BigDecimal authorizationBalance;

    @Column(name = "actual_balance", nullable = false, precision = 19, scale = 4)
    private BigDecimal actualBalance;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Version
    @Column(name = "version")
    private Long version;

    @OneToOne(mappedBy = "balance", fetch = FetchType.LAZY)
    private Account account;
}

