package faang.school.accountservice.entity;

import faang.school.accountservice.enums.CardType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "account_numbers_sequence")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AccountNumberSequence {
    @Id
    @Column(name = "type", nullable = false, length = 16)
    @Enumerated(value = EnumType.STRING)
    private CardType type;
    @Column(name = "count")
    private Long count;
}
