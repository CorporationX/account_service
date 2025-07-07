package faang.school.accountservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "free_account_numbers")
public class FreeAccountNumber {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated
    @Column(name = "account_balance_type", nullable = false)
    private AccountBalanceType accountBalanceType;
    @Column(name = "account_number", nullable = false, unique = true)
    @Size(min = 12, max = 20, message = "Account number must be between 12 and 20 characters")
    private String accountNumber;
}
