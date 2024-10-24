package faang.school.accountservice.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum AccountRate {
    LOW(0.35),
    MEDIUM(0.5),
    HIGH(0.725),
    PROMO(0.45);

    private final double rate;
}
