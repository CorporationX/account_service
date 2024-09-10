package faang.school.accountservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "balance")
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Balance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    @JoinColumn(name = "account_id")
    private Account account;
//    @Column(name = "account")
//    @OneToOne(mappedBy = "balance")
//    private Account account;
// TODO: прописать миграцию после merge, проверить связь и раскомментировать

    @Column(name = "authorization_balance_amount", nullable = false)
    private BigDecimal authorizationBalance;

    @Column(name = "balance_amount", nullable = false)
    private BigDecimal balance;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "version", nullable = false)
    private long version;
}