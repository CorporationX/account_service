package faang.school.accountservice.entity;

import faang.school.accountservice.enums.AccountType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "account_number_sequence")
public class AccountNumberSequence {

    @Id
    @Column(name = "type", nullable = false, length = 32)
    private AccountType type;

    @Column(name = "counter", nullable = false)
    private Long counter;

    @Transient
    private long initialValue;
}
