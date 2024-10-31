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
    import jakarta.persistence.Version;
    import lombok.Data;
    import lombok.NoArgsConstructor;
    import org.hibernate.annotations.CreationTimestamp;
    import org.hibernate.annotations.UpdateTimestamp;

    import jakarta.persistence.*;
    import lombok.Data;

    import java.time.LocalDateTime;
    import java.util.List;
    import java.math.BigDecimal;
    import java.time.LocalDateTime;

    @Entity
    @Data
    @Table(name = "balance")
    @NoArgsConstructor
    public class Balance {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private long id;

        @JoinColumn(name = "account_id", unique = true)
        @OneToOne(cascade = CascadeType.ALL)
        private Account account;

        @Column(name = "cur_auth_balance")
        private BigDecimal curAuthBalance;

        @Column(name = "cur_fact_balance")
        private BigDecimal curFactBalance;

        @Column(name = "created_at")
        @CreationTimestamp
        private LocalDateTime createdAt;

        @Column(name = "updated_at")
        @UpdateTimestamp
        private LocalDateTime updatedAt;

        @OneToMany(mappedBy = "balance", cascade = CascadeType.ALL)
        private List<BalanceAudit> audits;

        @Column(name = "version", nullable = false)
        @Version
        private int version;
    }
