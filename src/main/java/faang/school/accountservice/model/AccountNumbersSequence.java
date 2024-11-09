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
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "account_numbers_sequence")
public class AccountNumbersSequence {
    @Id
    @Column(name = "account_type", nullable = false, unique = true, length = 8)
    private String accountType;

    @Builder.Default
    @Column(name = "current_number", nullable = false)
    private Long currentNumber = 0L;
}
