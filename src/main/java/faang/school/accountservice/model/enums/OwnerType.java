package faang.school.accountservice.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OwnerType {
    USER(0),
    PROJECT(1);

    private final int value;
}
