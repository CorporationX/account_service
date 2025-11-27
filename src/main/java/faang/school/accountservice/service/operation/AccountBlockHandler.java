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
public class AccountBlockHandler implements OperationHandler {
    private final AccountService accountService;

    @Override
    public OperationType getSupportedOperationType() {
        return OperationType.ACCOUNT_BLOCK;
    }

    @Override
    public void execute(Request request) {
        log.info("Account blocking started: requestId={}", request.getIdempotencyToken());

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }

        String accountIdStr = request.getInputData().get("accountId").toString();
        UUID accountId = UUID.fromString(accountIdStr);

        ResponseAccountDto responseDto = accountService.updateAccountStatus(accountId, AccountStatus.BLOCKED);

        request.getInputData().put("blockedAt", LocalDateTime.now());
        log.info("Account blocked: id={}", accountId);
    }
}