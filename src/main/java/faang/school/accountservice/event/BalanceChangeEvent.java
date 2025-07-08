package faang.school.accountservice.event;

import faang.school.accountservice.model.AccountBalance;

import java.util.UUID;

public record BalanceChangeEvent(
        AccountBalance balance,
        UUID operationId
){}
