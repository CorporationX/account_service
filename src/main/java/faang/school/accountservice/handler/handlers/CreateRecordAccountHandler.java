package faang.school.accountservice.handler.handlers;

import faang.school.accountservice.dto.account.AccountDtoOpen;
import faang.school.accountservice.dto.account.AccountDtoResponse;
import faang.school.accountservice.entity.Request;
import faang.school.accountservice.entity.RequestTask;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OwnerType;
import faang.school.accountservice.enums.RequestHandler;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.service.account.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateRecordAccountHandler implements RequestTaskHandler {
    private final AccountService accountService;
    private final AccountRepository accountRepository;

    @Override
    public RequestHandler getHandlerId() {
        return RequestHandler.CREATE_ACCOUNT;
    }

    @Transactional
    @Override
    public void execute(Request request, RequestTask task) {
        log.info("Try create account for user with id: {}", request.getInputData().get("ownerId"));
        AccountDtoOpen accountDtoOpen = AccountDtoOpen.builder()
                .ownerId((Long) request.getInputData().get("ownerId"))
                .ownerType((OwnerType) request.getInputData().get("ownerType"))
                .accountType((AccountType) request.getInputData().get("accountType"))
                .currency((Currency) request.getInputData().get("currency"))
                .build();
        AccountDtoResponse accountDtoResponse = accountService.open(accountDtoOpen);
        request.getContext().put("accountId", accountDtoResponse.getId());
        log.info("Created account with id: {}", accountDtoResponse.getId());
    }

    @Override
    @Transactional
    public void rollback(Request request, RequestTask task) {
        log.info("Try rollback creation account with id: {}", request.getContext().get("accountId"));
        Long accountId = (Long) request.getContext().get("accountId");
        accountRepository.deleteById(accountId);
        log.info("Rollback creation account with id: {} was successful", accountId);
    }
}
