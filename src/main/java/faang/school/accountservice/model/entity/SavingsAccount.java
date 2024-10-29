package faang.school.accountservice.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "savings_account")
public class SavingsAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_number", unique = true, nullable = false)
    private String accountNumber;

    @OneToOne
    @JoinColumn(name = "account_number", referencedColumnName = "number", insertable = false, updatable = false)
    @JsonIgnore
    private Account account;

    @OneToMany(mappedBy = "savingsAccount")
    private List<TariffHistory> tariffHistory;

    @Column(name = "last_date_percent")
    private LocalDateTime lastDatePercent;

    @Version
    @Column(name = "version", insertable = false)
    private Long version;

    @Column(name = "created_at", insertable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false)
    private LocalDateTime updatedAt;
}
