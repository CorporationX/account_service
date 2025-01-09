package faang.school.accountservice.handler.handlers;

import faang.school.accountservice.entity.Request;
import faang.school.accountservice.entity.RequestTask;
import faang.school.accountservice.enums.RequestHandler;
import faang.school.accountservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class CheckAccountLimitHandler implements RequestTaskHandler {
    private final AccountRepository accountRepository;
    @Value("${account-service.max-accounts}")
    private int maxAccounts;

    @Override
    public RequestHandler getHandlerId() {
        return RequestHandler.CHECK_MAX_ACCOUNTS;
    }

    @Transactional
    @Override
    public void execute(Request request, RequestTask task) {
        log.info("Trying to check account limit for user with id: {}", request.getInputData().get("ownerId"));
        Map<String, Object> inputData = request.getInputData();
        Long ownerId = (Long) inputData.get("ownerId");
        Long accountCount = accountRepository.countByOwnerId(ownerId);
        if (accountCount >= maxAccounts) {
            throw new IllegalStateException("Account limit exceeded for user with id: " + ownerId);
        }
        log.info("Account limit check passed for user with id: {}", ownerId);
    }

    @Override
    public void rollback(Request request, RequestTask task) {
        log.info("Rollback not required for {}", getHandlerId());
    }
}
