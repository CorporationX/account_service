package faang.school.accountservice.service.request_task.handler.impl.create_account;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.AccountRequest;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.AccountOwner;
import faang.school.accountservice.entity.Request;
import faang.school.accountservice.entity.RequestTask;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.request_task.RequestTaskStatus;
import faang.school.accountservice.enums.request_task.RequestTaskType;
import faang.school.accountservice.exception.JsonMappingException;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.service.AccountOwnerService;
import faang.school.accountservice.service.FreeAccountNumbersService;
import faang.school.accountservice.service.request.RequestService;
import faang.school.accountservice.service.request_task.handler.RequestTaskHandler;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateAccount implements RequestTaskHandler {

    private final ObjectMapper objectMapper;
    private final FreeAccountNumbersService numbersService;
    private final AccountOwnerService accountOwnerService;
    private final AccountRepository accountRepository;
    private final RequestService requestService;

    private final CheckAccountsQuantity checkAccountsQuantity;

    @Transactional
    @Retryable(
            retryFor = OptimisticLockingFailureException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    @Override
    public void execute(Request request) {
        try {
            AccountRequest accountRequest = mapAccountRequest(request);
            String number = numbersService.getFreeAccountNumber(accountRequest.getType());
            AccountOwner owner = accountOwnerService.findOwner(accountRequest.getOwnerId(),
                    accountRequest.getOwnerType());

            Account account = Account.builder()
                    .accountNumber(number)
                    .type(accountRequest.getType())
                    .currency(accountRequest.getCurrency())
                    .status(AccountStatus.ACTIVE)
                    .owner(owner)
                    .build();

            Account savedAccount = accountRepository.save(account);
            request.setContext(savedAccount.getId().toString());
            setRequestTaskStatusAndContext(request, RequestTaskStatus.DONE,
                    savedAccount.getId().toString());
            requestService.updateRequest(request);
            log.info("Finished processing request task with type: {}",
                    RequestTaskType.WRITE_INTO_ACCOUNT);

        } catch (OptimisticLockingFailureException e) {
            log.error("Optimistic locking failed after 3 retries for request with id: {}. " +
                    "Executing rollback.", request.getIdempotentToken(), e);
            rollback(request);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error occurred during execution request with id: {}. " +
                    "Executing rollback.", request.getIdempotentToken(), e);
            rollback(request);
            throw e;
        }
    }

    @Override
    public long getHandlerId() {
        return 2;
    }

    @Transactional
    @Override
    public void rollback(Request request) {
        RequestTask requestTask = request.getRequestTasks().stream()
                .filter(task -> task.getHandler().equals(RequestTaskType.WRITE_INTO_ACCOUNT))
                .findFirst().orElseThrow(() -> new EntityNotFoundException("No request task found"));

        if (requestTask.getRollbackContext() != null) {
            accountRepository.deleteById(Long.getLong(requestTask.getRollbackContext()));
        }

        List<RequestTask> tasks = request.getRequestTasks().stream()
                .filter(task -> task.getStatus() == RequestTaskStatus.DONE).toList();
        if (!tasks.isEmpty()) {
            setRequestTasksStatus(request, RequestTaskStatus.AWAITING);
            checkAccountsQuantity.rollback(request);
        }
        log.info("Request task with type: {}, id: {} rollback",
                RequestTaskType.WRITE_INTO_ACCOUNT, requestTask.getId());
    }

    private AccountRequest mapAccountRequest(Request request) {
        AccountRequest accountRequest;
        try {
            accountRequest = objectMapper.readValue(request.getContext(), AccountRequest.class);
        } catch (JsonProcessingException e) {
            throw new JsonMappingException(e.getMessage());
        }
        return accountRequest;
    }

    private void setRequestTasksStatus(Request request, RequestTaskStatus status) {
        request.getRequestTasks()
                .forEach(requestTask -> requestTask.setStatus(status));
    }

    private void setRequestTaskStatusAndContext(
            Request request, RequestTaskStatus requestTaskStatus, String taskContext) {
        request.getRequestTasks().stream()
                .filter(requestTask -> requestTask.getHandler().
                        equals(RequestTaskType.WRITE_INTO_ACCOUNT))
                .forEach(requestTask -> {
                    requestTask.setStatus(requestTaskStatus);
                    requestTask.setRollbackContext(taskContext);
                });
    }
}
