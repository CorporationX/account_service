package faang.school.accountservice.service.operation;

import faang.school.accountservice.dto.account.CreateAccountDto;
import faang.school.accountservice.dto.account.ResponseAccountDto;
import faang.school.accountservice.entity.request.Request;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.request.OperationType;
import faang.school.accountservice.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountCreateHandler implements OperationHandler {

    private final AccountService accountService;

    @Override
    public OperationType getSupportedOperationType() {
        return OperationType.ACCOUNT_CREATE;
    }

    @Override
    public void execute(Request request) {

        log.info("Account creation started: user={}, project={}, requestId={}",
                request.getUserId(), request.getProjectId(), request.getIdempotencyToken());

        CreateAccountDto createDto = new CreateAccountDto(
                request.getUserId(),
                request.getProjectId(),
                AccountType.valueOf((String) request.getInputData().get("accountType")),
                Currency.valueOf((String) request.getInputData().get("currency"))
        );

        ResponseAccountDto responseDto = accountService.createAccount(createDto);

        Map<String, Object> inputData = request.getInputData();
        inputData.put("accountId", responseDto.accountId());
        inputData.put("accountNumber", responseDto.accountNumber());

        log.info("Account created: id={}, number={}", responseDto.accountId(), responseDto.accountNumber());
    }

}