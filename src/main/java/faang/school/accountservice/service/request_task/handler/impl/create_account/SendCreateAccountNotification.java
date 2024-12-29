package faang.school.accountservice.service.request_task.handler.impl.create_account;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.Request;
import faang.school.accountservice.enums.request.RequestStatus;
import faang.school.accountservice.enums.request_task.RequestTaskStatus;
import faang.school.accountservice.enums.request_task.RequestTaskType;
import faang.school.accountservice.event.CreateAccountEvent;
import faang.school.accountservice.exception.JsonMappingException;
import faang.school.accountservice.publisher.CreateAccountPublisher;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.service.request.RequestService;
import faang.school.accountservice.service.request_task.handler.RequestTaskHandler;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SendCreateAccountNotification implements RequestTaskHandler {

    private final ObjectMapper objectMapper;
    private final CreateAccountPublisher publisher;
    private final RequestService requestService;
    private final AccountRepository accountRepository;

    @Override
    public void execute(Request request) {
        Account account;
        try {
            account = objectMapper.readValue(request.getContext(), Account.class);
        } catch (JsonProcessingException e) {
            throw new JsonMappingException(e.getMessage());
        }
        Account accountWithOwner = accountRepository.findById(account.getId()).orElseThrow(() ->
                new EntityNotFoundException("Account with id " + account.getId() + " not found"));

        CreateAccountEvent event = CreateAccountEvent.builder()
                .ownerId(accountWithOwner.getOwner().getId())
                .ownerType(accountWithOwner.getOwner().getOwnerType().name())
                .accountType(account.getType().name())
                .currency(account.getCurrency().name())
                .build();

        request.setContext(null);
        request.setRequestStatus(RequestStatus.DONE);
        request.getRequestTasks().stream()
                .filter(requestTask -> requestTask.getHandler().
                        equals(RequestTaskType.SEND_CREATE_ACCOUNT_NOTIFICATION))
                .forEach(requestTask -> requestTask.setStatus(RequestTaskStatus.DONE));

        requestService.updateRequest(request);
        publisher.publish(event);
        log.info("Finished processing request task with type: {}",
                RequestTaskType.SEND_CREATE_ACCOUNT_NOTIFICATION);
        log.info("Successfully opened account with number: {}", account.getAccountNumber());
    }

    @Override
    public long getHandlerId() {
        return 5;
    }
}
