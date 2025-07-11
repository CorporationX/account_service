package faang.school.accountservice.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
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
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "savings_account")
@DiscriminatorValue("SAVINGS")
@PrimaryKeyJoinColumn(foreignKey = @ForeignKey(name = "fk_savings_account_account"))
public class SavingsAccount extends Account{

    @OneToMany(mappedBy = "savingsAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SavingsAccountTariff> tariffStory = new ArrayList<>();

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
}
