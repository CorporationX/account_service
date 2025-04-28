package faang.school.accountservice.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CardType {
    DEBIT(4200_0000_0000_0000L),
    CREDIT(5236_0000_0000_0000L);

    private final long cardPattern;
}
