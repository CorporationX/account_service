package faang.school.accountservice.model;


import faang.school.accountservice.enums.DmsTypeOperation;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "reserve")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reserve {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "request_id", nullable = false, unique = true)
    Long requestId;

    @ManyToOne
    @JoinColumn(name = "sender_account_id", nullable = false)
    Account senderAccount;

    @ManyToOne
    @JoinColumn(name = "receiver_account_id", nullable = false)
    Account receiverAccount;

    @Column(name = "amount", nullable = false)
    BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    DmsTypeOperation status;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "clear_scheduled_at", nullable = false)
    LocalDateTime clearScheduledAt;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    LocalDateTime createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at", nullable = false)
    LocalDateTime updatedAt;
}
