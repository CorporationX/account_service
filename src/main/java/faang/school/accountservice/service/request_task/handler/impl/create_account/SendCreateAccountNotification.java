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
import faang.school.accountservice.service.request.RequestService;
import faang.school.accountservice.service.request_task.handler.RequestTaskHandler;
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

    @Override
    public void execute(Request request) {
        Account account;
        try {
            account = objectMapper.readValue(request.getContext(), Account.class);
        } catch (JsonProcessingException e) {
            throw new JsonMappingException(e.getMessage());
        }
        CreateAccountEvent event = CreateAccountEvent.builder()
                .ownerId(account.getOwner().getId())
                .ownerType(account.getOwner().getOwnerType())
                .accountType(account.getType())
                .currency(account.getCurrency())
                .build();
        publisher.publish(event);

        request.setContext(null);
        request.setRequestStatus(RequestStatus.DONE);
        request.getRequestTasks().stream()
                .filter(requestTask -> requestTask.getHandler().
                        equals(RequestTaskType.SEND_CREATE_ACCOUNT_NOTIFICATION))
                .forEach(requestTask -> requestTask.setStatus(RequestTaskStatus.DONE));

        requestService.updateRequest(request);
        log.info("Finished processing request task with type: {}",
                RequestTaskType.SEND_CREATE_ACCOUNT_NOTIFICATION);
        log.info("Successfully opened account with number: {}", account.getAccountNumber());
    }

    @Override
    public long getHandlerId() {
        return 5;
    }
}
