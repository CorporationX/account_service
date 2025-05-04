package faang.school.accountservice.entity;

import faang.school.accountservice.enums.AccountType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "account_numbers_sequence")
@Data
@NoArgsConstructor
public class AccountSequence {

    @Id
    @Column(name = "type", nullable = false, length = 32)
    @Enumerated(EnumType.STRING)
    private AccountType type;

    @Column(name = "counter", nullable = false)
    private Long counter;

    @Version
    private Long version;
}
