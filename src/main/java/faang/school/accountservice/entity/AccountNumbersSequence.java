package faang.school.accountservice.entity;

import faang.school.accountservice.enums.AccountType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "account_numbers_sequence")
public class AccountNumbersSequence {

    @Id
    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    @Column(name = "current_counter", nullable = false)
    private long currentCounter;

    @Version
    private Long version;
}
