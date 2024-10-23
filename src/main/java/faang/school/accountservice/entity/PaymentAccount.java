package faang.school.accountservice.entity;

import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OwnerType;
import faang.school.accountservice.enums.PaymentAccountStatus;
import faang.school.accountservice.enums.PaymentAccountType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "account")
public class PaymentAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min = 12, max = 20, message = "min payment account number length  must be min 12 max 20")
    @Column(name = "number", length = 20, nullable = false)
    private String number;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @Column(name = "owner_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private OwnerType ownerType;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentAccountType type;

    @Column(name = "balance", nullable = false)
    private Long balance;

    @Column(name = "currency", nullable = false)
    @Enumerated(EnumType.STRING)
    private Currency currency;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentAccountStatus status;

    @Column(name = "create_date", nullable = false)
    private LocalDateTime createDate;

    @Column(name = "change_date", nullable = false)
    private LocalDateTime changeDate;

    @Column(name = "close_date")
    private LocalDateTime closeDate;

    @Column(name = "version", nullable = false)
    @Version
    private Long version;
}
