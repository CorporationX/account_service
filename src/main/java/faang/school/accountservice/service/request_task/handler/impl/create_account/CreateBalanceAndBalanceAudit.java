package faang.school.accountservice.service.request_task.handler.impl.create_account;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.dto.rollbeck.RollbackTaskCrateBalanceDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.BalanceAudit;
import faang.school.accountservice.entity.Request;
import faang.school.accountservice.entity.RequestTask;
import faang.school.accountservice.enums.request_task.RequestTaskStatus;
import faang.school.accountservice.enums.request_task.RequestTaskType;
import faang.school.accountservice.exception.JsonMappingException;
import faang.school.accountservice.service.BalanceAuditService;
import faang.school.accountservice.service.BalanceService;
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
public class CreateBalanceAndBalanceAudit implements RequestTaskHandler {

    private final ObjectMapper objectMapper;
    private final BalanceService balanceService;
    private final RequestService requestService;
    private final BalanceAuditService balanceAuditService;

    private final CheckAccountsQuantity checkAccountsQuantity;
    private final CreateAccount createAccount;

    @Transactional
    @Retryable(
            retryFor = OptimisticLockingFailureException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    @Override
    public void execute(Request request) {
        try {
            Account account = mapAccount(request);
            BalanceDto balanceDto = balanceService.createBalance(account);
            List<BalanceAudit> audits = account.getBalanceAudits();
            List<Long> auditsIds = audits.stream()
                    .map(BalanceAudit::getId).toList();

            RollbackTaskCrateBalanceDto context = RollbackTaskCrateBalanceDto.builder()
                    .balanceId(balanceDto.getId())
                    .balanceAuditIds(auditsIds)
                    .build();

            String rollbackContext = mapRollbackTaskDtoToString(context);
            setRequestTaskStatusAndContext(request, RequestTaskStatus.DONE, rollbackContext);
            requestService.updateRequest(request);
            log.info("Finished processing request task with type: {}",
                    RequestTaskType.WRITE_INTO_BALANCE_BALANCE_AUDIT);
        } catch (OptimisticLockingFailureException e) {
            log.error("Optimistic locking failed after 3 retries for request with id: {}. " +
                    "Executing rollback.", request.getIdempotentToken(), e);
            rollback(request);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error occurred during execution request with id: {}. " +
                    "Executing rollback.",request.getIdempotentToken(), e);
            rollback(request);
        }
    }

    @Override
    public long getHandlerId() {
        return 3;
    }

    @Override
    public void rollback(Request request) {
        RequestTask requestTask = request.getRequestTasks().stream()
                .filter(task -> task.getHandler().equals(RequestTaskType.WRITE_INTO_BALANCE_BALANCE_AUDIT))
                .findFirst().orElseThrow(() -> new EntityNotFoundException("No request task found"));

        RollbackTaskCrateBalanceDto dto = mapRollbackTaskDto(requestTask.getRollbackContext());
        balanceService.deleteBalance(dto.getBalanceId());
        dto.getBalanceAuditIds().forEach(balanceAuditService::deleteAudit);
        setRequestTaskStatusAndContext(request, RequestTaskStatus.AWAITING, null);

        List<RequestTask> tasks = request.getRequestTasks().stream()
                .filter(task -> task.getStatus() == RequestTaskStatus.DONE).toList();
        if (!tasks.isEmpty()) {
            checkAccountsQuantity.rollback(request);
            createAccount.rollback(request);
        }
        log.info("Request task with type: {} rollback",RequestTaskType.WRITE_INTO_BALANCE_BALANCE_AUDIT);
    }

    private String mapRollbackTaskDtoToString(RollbackTaskCrateBalanceDto dto) {
        String rollbackContext;
        try {
            rollbackContext = objectMapper.writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            throw new JsonMappingException(e.getMessage());
        }
        return rollbackContext;
    }

    private RollbackTaskCrateBalanceDto mapRollbackTaskDto(String taskContext) {
        RollbackTaskCrateBalanceDto dto;
        try {
            dto = objectMapper.readValue(taskContext, RollbackTaskCrateBalanceDto.class);
        } catch (JsonProcessingException e) {
            throw new JsonMappingException(e.getMessage());
        }
        return dto;
    }

    private Account mapAccount(Request request) {
        Account account;
        try {
            account = objectMapper.readValue(request.getContext(), Account.class);
        } catch (JsonProcessingException e) {
            throw new JsonMappingException(e.getMessage());
        }
        return account;
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
