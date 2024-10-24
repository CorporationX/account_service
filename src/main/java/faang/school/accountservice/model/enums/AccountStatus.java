package faang.school.accountservice.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AccountStatus {
    ACTIVE(0),
    BLOCKED(1),
    CLOSED(2);

    private final int value;
}
