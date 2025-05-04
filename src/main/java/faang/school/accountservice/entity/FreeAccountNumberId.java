package faang.school.accountservice.entity;

import faang.school.accountservice.enums.CardType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class FreeAccountNumberId {
    @Column(name = "type", nullable = false, length = 16)
    @Enumerated(value = EnumType.STRING)
    private CardType cardType;
    @Column(name = "number", nullable = false)
    private Long cardNumber;
}
