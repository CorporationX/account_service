package faang.school.accountservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "account_numbers_sequence")
public class AccountNumbersSequence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_type", nullable = false, unique = true)
    private String accountType;

    @Column(name = "current_number", nullable = false)
    private BigInteger currentNumber;

    @Column(name = "version", nullable = false)
    @Version
    private Long version;
}