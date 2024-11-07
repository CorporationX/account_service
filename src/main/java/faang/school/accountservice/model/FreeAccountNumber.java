package faang.school.accountservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@Getter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "free_account_numbers")
public class FreeAccountNumber {
    @Id
    @Column(name = "account_number", nullable = false, length = 20)
    private String accountNumber;

    @Column(name = "account_type", nullable = false, updatable = false, length = 8)
    private String accountType;
}
