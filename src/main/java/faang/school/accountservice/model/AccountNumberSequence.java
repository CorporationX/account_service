package faang.school.accountservice.model;

import faang.school.accountservice.enums.AccountType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "account_numbers_sequence")
public class AccountNumberSequence {

    @Id
    @Column(name = "account_type", length = 24, nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    @Column(name = "counter", nullable = false)
    @Max(9999_9999_9998L)
    private Long counter;

    @Version
    private Long version;
}
