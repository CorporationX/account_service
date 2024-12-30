package faang.school.accountservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "account_numbers_sequence")
@Data
@NoArgsConstructor
public class AccountSequence {
    @Id
    @Column(name = "type", nullable = false, length = 32)
    private AccountType type;
    @Column(name = "counter", nullable = false)
    private long counter;

    @Transient
    private long initialCounter;
}
