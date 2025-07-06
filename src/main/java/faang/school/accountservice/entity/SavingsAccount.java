package faang.school.accountservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
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
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "savings_account")
@DiscriminatorValue("SAVINGS")
public class SavingsAccount extends Account{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "account_id", nullable = false, unique = true)
    private Account account;

    // todo: юник констрейнт, что каждое из значений должно быть id тарифа?

// История тарифов — список (последовательность) id тарифов, отображающий, как менялись тарифы у накопительного счета.
// Последнее значение в списке является текущим тарифом.
// Например: [1, 2, 1, 3].
// Храним через JSON в текстовом столбце или в отдельной таблице.

//    почему не храним строкой? Или что значит "JSON в текстовом столбце"?
    @Column(name = "tariff_story", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private List<Long> tariffStory;


    @Column(name = "last_interest_accrual_at")
    private LocalDateTime lastInterestAccrualAt;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Version
    @Column(name = "version")
    private Integer version;
}
