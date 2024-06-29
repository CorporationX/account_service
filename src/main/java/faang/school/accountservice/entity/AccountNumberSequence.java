package faang.school.accountservice.entity;

import faang.school.accountservice.enums.AccountType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "account_numbers_sequence")
public class AccountNumberSequence {

    @Id
    @Column(name = "account_type", length = 64, nullable = false, unique = true)
    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    @Column(name = "current_counter", nullable = false)
    private Long currentCounter;

    @Column(name = "previous_counter", nullable = false)
    private Long previousCounter;

    @Version
    @Column(name = "version")
    private Long version;
}