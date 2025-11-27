package faang.school.accountservice.service.operation;

import faang.school.accountservice.dto.account.ResponseAccountDto;
import faang.school.accountservice.entity.request.Request;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.request.OperationType;
import faang.school.accountservice.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountUpdateHandler implements OperationHandler {

    private final AccountService accountService;

    @Override
    public OperationType getSupportedOperationType() {
        return OperationType.ACCOUNT_UPDATE;
    }

    @Override
    public void execute(Request request) {
        log.info("Account updating started: requestId={}", request.getIdempotencyToken());

        String accountIdStr = request.getInputData().get("accountId").toString();
        UUID accountId = UUID.fromString(accountIdStr);

        AccountStatus newStatus = AccountStatus.valueOf((String) request.getInputData().get("newStatus"));
        ResponseAccountDto responseDto = accountService.updateAccountStatus(accountId, newStatus);

        request.getInputData().put("updatedAt", LocalDateTime.now());
        log.info("Account updated: id={}", accountId);
    }
}
