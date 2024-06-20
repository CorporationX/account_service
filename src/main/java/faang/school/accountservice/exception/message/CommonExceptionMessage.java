package faang.school.accountservice.exception.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommonExceptionMessage {
    NON_EXISTING_USER_EXCEPTION("No such user detected in system for passed user id."),
    NON_EXISTING_PROJECT_EXCEPTION("No such project detected in system for passed project id.");

    private final String message;
}
