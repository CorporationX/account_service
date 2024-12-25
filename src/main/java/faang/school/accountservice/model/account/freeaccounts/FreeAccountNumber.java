package faang.school.accountservice.model.account.freeaccounts;

import faang.school.accountservice.enums.AccountType;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "free_account_numbers", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"type", "account_number"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FreeAccountNumber {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 32)
    private AccountType type;

    @Column(name = "account_number", nullable = false, length = 20)
    private String accountNumber;
}
