package faang.school.accountservice.model;

import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.payment.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

@Data
@Entity
@Table(name = "payment_history")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentHistory implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "requester_number", nullable = false)
    private BigInteger requesterNumber;

    @Column(name = "receiver_number", nullable = false)
    private BigInteger receiverNumber;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Currency currency;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus type;

    @Column(name = "external_payment_key", nullable = false)
    private Long externalPaymentKey;

    @Column(name = "idempotency_key", nullable = false, unique = true)
    private String idempotencyKey;
}