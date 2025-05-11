package faang.school.accountservice.entity;

import faang.school.accountservice.enums.AccountNumberType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "account_numbers_sequence")
@Data
@NoArgsConstructor
public class AccountNumbersSequence {

    @Id
    @Enumerated(value = EnumType.STRING)
    @Column(name = "type", nullable = false, length = 16)
    private AccountNumberType type;

    @Column(name = "counter", nullable = false)
    private long counter;
}
