package faang.school.accountservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cashback_tariff")
public class CashbackTariff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToMany(mappedBy = "cashbackTariff")
    private Set<Account> accounts = new HashSet<>();

    @OneToMany(mappedBy = "cashbackTariff")
    private Set<CashbackOperationMapping> operationMappings = new HashSet<>();

    @OneToMany(mappedBy = "cashbackTariff")
    private Set<CashbackMerchantMapping> merchantMappings = new HashSet<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
