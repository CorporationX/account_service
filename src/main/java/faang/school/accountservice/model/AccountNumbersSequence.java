package faang.school.accountservice.model;

import faang.school.accountservice.enums.AccountType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "account_numbers_sequence")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountNumbersSequence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "accounttype", nullable = false, unique = true)
    private AccountType accountType;

    @Column(name = "current_value", nullable = false)
    private Long currentValue;

    @Version
    @Column(name = "version")
    private Long version;
}
