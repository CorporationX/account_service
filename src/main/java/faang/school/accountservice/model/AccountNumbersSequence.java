package faang.school.accountservice.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "account_numbers_sequence")
public class AccountNumbersSequence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "number", nullable = false)
    private long number;
    @Column(name = "account_type_id", nullable = false)
    private long accountTypeId;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "account_type_id", referencedColumnName = "id")
    private AccountType accountType;
}
