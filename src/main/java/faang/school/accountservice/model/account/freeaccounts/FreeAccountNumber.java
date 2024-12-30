package faang.school.accountservice.model.account.freeaccounts;

import faang.school.accountservice.enums.AccountType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


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

    public FreeAccountNumber(AccountType type, String accountNumber) {
        this.type = type;
        this.accountNumber = accountNumber;
    }
}
