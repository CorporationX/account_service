    package faang.school.accountservice.entity;

    import jakarta.persistence.*;
    import lombok.Data;

    import java.time.LocalDateTime;
    import java.util.List;

    @Entity
    @Data
    @Table(name = "balance")
    public class Balance {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private long id;

        @JoinColumn(name = "account_id", unique = true)
        @OneToOne(cascade = CascadeType.ALL)
        private Account account;

        @Column(name = "cur_auth_balance")
        private double curAuthBalance;

        @Column(name = "cur_fact_balance")
        private double curFactBalance;

        @Column(name = "created_at")
        private LocalDateTime createdAt;

        @Column(name = "updated_at")
        private LocalDateTime updatedAt;

        @Column(name = "version", nullable = false)
        private int version;

        @OneToMany(mappedBy = "balance", cascade = CascadeType.ALL)
        private List<BalanceAudit> audits;

        public void nextVersion() {
            this.version++;
        }
    }
