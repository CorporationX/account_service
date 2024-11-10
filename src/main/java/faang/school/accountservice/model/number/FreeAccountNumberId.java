package faang.school.accountservice.model.number;

import faang.school.accountservice.enums.AccountNumberType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Getter
class FreeAccountNumberId {

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 128)
    private AccountNumberType type;


    @Column(name = "digit_sequence", nullable = false, length = 20)
    private String digitSequence;
}