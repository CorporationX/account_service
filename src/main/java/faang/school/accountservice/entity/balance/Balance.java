package faang.school.accountservice.entity.balance;

import faang.school.accountservice.entity.account.Account;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "balance")
public class Balance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "authorisation_balance", nullable = false)
    private long authorisationBalance;

    @Column(name = "actual_balance", nullable = false)
    private long actualBalance;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "version")
    @Version
    private long version;

    @OneToOne
    @JoinColumn(name = "account_id", insertable = false, updatable = false)
    private Account account;
}

