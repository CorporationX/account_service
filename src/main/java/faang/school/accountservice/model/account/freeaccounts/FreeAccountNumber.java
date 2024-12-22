package faang.school.accountservice.model.account.freeaccounts;

import faang.school.accountservice.model.account.AccountType;
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
public class FreeAccountNumber {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 32)
    private AccountType type;

    @Column(name = "account_number", nullable = false)
    private Long accountNumber;
}

//
//package faang.school.accountservice.model.account.freeaccounts;
//
//import jakarta.persistence.EmbeddedId;
//import jakarta.persistence.Entity;
//import jakarta.persistence.Table;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//@Entity
//@Table(name = "free_account_numbers")
//@Data
//@NoArgsConstructor
//public class FreeAccountNumber {
//
//    @EmbeddedId
//    private FreeAccountId id;
//}
//
