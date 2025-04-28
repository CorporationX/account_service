package faang.school.accountservice.entity;

import faang.school.accountservice.enums.CardType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "account_numbers_sequence")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountNumberSequence {
    @Id
    @Column(name = "type", nullable = false, length = 16)
    @Enumerated(value = EnumType.STRING)
    private CardType type;
    @Column(name = "count")
    private long count;
}
