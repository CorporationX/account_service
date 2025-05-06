package faang.school.accountservice.service.account;

import faang.school.accountservice.entity.enums.Status;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AccountAction {
    BLOCKED(Status.BLOCKED),
    UNBLOCKED(Status.ACTIVE),
    CLOSED(Status.CLOSED);

    private final Status status;

}