package faang.school.accountservice.model.entity;

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
    private Long id;

    @OneToOne
    @JoinColumn(name = "number", insertable = false)
    private Account account;

    @OneToMany(mappedBy = "savingsAccount")
    private List<TariffHistory> tariffHistory;

    @Column(name = "last_date_percent")
    private LocalDateTime lastDatePercent;

    @Version
    @Column(name = "version")
    private Long version;

    @Column(name = "created_at", insertable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false)
    private LocalDateTime updatedAt;
}
