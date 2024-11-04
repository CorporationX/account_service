package faang.school.accountservice.model.number;

import faang.school.accountservice.enums.AccountNumberType;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
class FreeAccountNumberId implements Serializable {
    private AccountNumberType type;
    private String digitSequence;
}