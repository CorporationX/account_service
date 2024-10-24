package faang.school.accountservice.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Currency {
    USD(0),
    EUR(1),
    RUB(2);

    private final int value;
}
