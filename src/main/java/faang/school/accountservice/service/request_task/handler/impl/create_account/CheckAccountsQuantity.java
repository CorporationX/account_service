package faang.school.accountservice.service.request_task.handler.impl.create_account;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.AccountRequest;
import faang.school.accountservice.entity.AccountOwner;
import faang.school.accountservice.entity.Request;
import faang.school.accountservice.enums.request.RequestStatus;
import faang.school.accountservice.enums.request_task.RequestTaskStatus;
import faang.school.accountservice.enums.request_task.RequestTaskType;
import faang.school.accountservice.exception.JsonMappingException;
import faang.school.accountservice.service.AccountOwnerService;
import faang.school.accountservice.service.request.RequestService;
import faang.school.accountservice.service.request_task.handler.RequestTaskHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CheckAccountsQuantity implements RequestTaskHandler {

    private final ObjectMapper objectMapper;
    private final AccountOwnerService accountOwnerService;
    private final RequestService requestService;

    @Value("${account.max-accounts-quantity}")
    private int maxAccountsQuantity;

    @Override
    public void execute(Request request) {
        AccountRequest accountRequest;
        try {
            accountRequest = objectMapper.readValue(request.getContext(), AccountRequest.class);
            log.info("Start opening a new account for ownerId: {}, ownerType: {}",
                    accountRequest.getOwnerId(), accountRequest.getOwnerType());
        } catch (JsonProcessingException e) {
            throw new JsonMappingException(e.getMessage());
        }

        AccountOwner owner = accountOwnerService.
                findOwner(accountRequest.getOwnerId(), accountRequest.getOwnerType());

        if (owner.getAccounts().size() >= maxAccountsQuantity) {
            throw new IllegalStateException("Forbidden have more than " +
                    maxAccountsQuantity + " accounts");
        }
        request.setRequestStatus(RequestStatus.PROCESSING);
        request.getRequestTasks().stream()
                .filter(requestTask -> requestTask.getHandler().
                        equals(RequestTaskType.CHECK_ACCOUNTS_QUANTITY))
                .forEach(requestTask -> requestTask.setStatus(RequestTaskStatus.DONE));
        requestService.updateRequest(request);
        log.info("Finished processing request task with type: {}", RequestTaskType.CHECK_ACCOUNTS_QUANTITY);
    }

    @Override
    public long getHandlerId() {
        return 1;
    }
}
