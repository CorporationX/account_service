    package faang.school.accountservice.entity;

    import jakarta.persistence.CascadeType;
    import jakarta.persistence.Column;
    import jakarta.persistence.Entity;
    import jakarta.persistence.GeneratedValue;
    import jakarta.persistence.GenerationType;
    import jakarta.persistence.Id;
    import jakarta.persistence.JoinColumn;
    import jakarta.persistence.OneToOne;
    import jakarta.persistence.Table;
    import lombok.Data;

    import java.time.LocalDateTime;

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

        public void nextVersion() {
            this.version++;
        }
    }
