package faang.school.accountservice.enums;

import lombok.Getter;

@Getter
public enum RequestStatus {
    OPEN,
    PENDING,
    COMPLETED,
    CANCELLED,

/*IN_PROGRESS,
FAILED,
COUNT_ACCOUNTS,
CREATE_ACCOUNT_RECORD,
CREATE_BALANCE_RECORD,
CREATE_AUDIT_RECORD,
CREATE_CASHBACK_RECORD,
SEND_NOTIFICATION,
COMPLETED*/
}
