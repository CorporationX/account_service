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
@Table(name = "free_account_numbers")
public class FreeAccountNumber {

    @Column(name = "type", length = 64, nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountType type;

    @Id
    @Column(name = "account_number", nullable = false, unique = true)
    private long accountNumber;
}
