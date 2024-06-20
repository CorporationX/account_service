package faang.school.accountservice.exception.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AccountExceptionMessage {
    CLOSED_ACCOUNT_CREATE_EXCEPTION("Closed account cannot be created. Only existing account can be closed."),
    NO_OWNER_EXCEPTION("An account must have one owner."),
    TWO_OWNERS_EXCEPTION("An account cannot have two owner (or user owner or project owner)."),
    NON_EXISTING_ACCOUNT_EXCEPTION("There is no account with id: %d");

    private final String message;
}
