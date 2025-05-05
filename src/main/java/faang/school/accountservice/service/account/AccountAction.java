package faang.school.accountservice.service.account;

import faang.school.accountservice.entity.enums.Status;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AccountAction {
    BLOCKED(Status.BLOCKED, "blocked"),
    UNBLOCKED(Status.ACTIVE, "unblocked"),
    CLOSED(Status.CLOSED, "closed");

    private final Status status;
    private final String description;
}