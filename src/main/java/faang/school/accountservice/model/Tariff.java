package faang.school.accountservice.model;

import faang.school.accountservice.enums.TariffType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "public.tariff")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tariff {
    @Id
    private Long id;

    @Column(name = "type", nullable = false, length = 128)
    private TariffType type;

    @Column(name = "rate", nullable = false)
    private Double rate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @ManyToMany
    @JoinTable(
            name = "public.tariff_history",
            joinColumns = @JoinColumn(name = "tariff_id"),
            inverseJoinColumns = @JoinColumn(name = "savings_account_id")
    )
    private List<SavingsAccount> savingsAccounts;
}
