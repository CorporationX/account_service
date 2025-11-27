package faang.school.accountservice.service.operation;

import faang.school.accountservice.dto.account.ResponseAccountDto;
import faang.school.accountservice.entity.request.Request;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.request.OperationType;
import faang.school.accountservice.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountCloseHandler implements OperationHandler {

    private final AccountService accountService;

    @Override
    public OperationType getSupportedOperationType() {
        return OperationType.ACCOUNT_CLOSE;
    }

    @Override
    public void execute(Request request) {
        log.info("Account closure started: requestId={}", request.getIdempotencyToken());

        String accountIdStr = request.getInputData().get("accountId").toString();
        UUID accountId = UUID.fromString(accountIdStr);

        ResponseAccountDto responseDto = accountService.updateAccountStatus(accountId, AccountStatus.CLOSED);

        request.getInputData().put("closedAt", responseDto.closedAt());
        log.info("Account closed: id={}", accountId);
    }
}