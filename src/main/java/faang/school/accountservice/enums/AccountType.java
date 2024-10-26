package faang.school.accountservice.enums;

import lombok.Getter;

import java.util.List;

@Getter
public enum AccountType {
    CURRENCY_ACCOUNT(List.of(OwnerType.USER, OwnerType.PROJECT)),
    INDIVIDUAL_ACCOUNT(List.of(OwnerType.USER)),
    LEGAL_ENTITY_ACCOUNT(List.of(OwnerType.PROJECT)),

    ;

    private final List<OwnerType> allowedOwnerTypes;

    AccountType(List<OwnerType> allowedOwnerTypes) {
        this.allowedOwnerTypes = allowedOwnerTypes;
    }
}
