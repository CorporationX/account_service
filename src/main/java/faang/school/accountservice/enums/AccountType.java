package faang.school.accountservice.enums;

import lombok.Getter;

import java.util.Set;

@Getter
public enum AccountType {
    CURRENCY_ACCOUNT(Set.of(OwnerType.USER, OwnerType.PROJECT)),
    INDIVIDUAL_ACCOUNT(Set.of(OwnerType.USER)),
    LEGAL_ENTITY_ACCOUNT(Set.of(OwnerType.PROJECT)),

    ;

    private final Set<OwnerType> allowedOwnerTypes;

    AccountType(Set<OwnerType> allowedOwnerTypes) {
        this.allowedOwnerTypes = allowedOwnerTypes;
    }
}
