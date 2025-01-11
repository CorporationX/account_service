package faang.school.accountservice.handler.handlers;

import faang.school.accountservice.entity.Request;
import faang.school.accountservice.entity.RequestTask;
import faang.school.accountservice.enums.RequestHandler;
import faang.school.accountservice.model.account.Account;
import faang.school.accountservice.publisher.account.AccountCreateEvent;
import faang.school.accountservice.publisher.account.AccountCreateEventPublisher;
import faang.school.accountservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SendNotificationHandler implements RequestTaskHandler {
    private final AccountRepository accountRepository;
    private final AccountCreateEventPublisher publisher;

    @Override
    public RequestHandler getHandlerId() {
        return RequestHandler.SEND_NOTIFICATION;
    }

    @Override
    public void execute(Request request, RequestTask task) {
        Long accountId = (Long) request.getContext().get("accountId");
        Account account = accountRepository.getAccountById(accountId);
        AccountCreateEvent event = AccountCreateEvent.builder()
                .accountId(account.getId())
                .ownerId(account.getOwner().getOwnerId())
                .createdAt(account.getCreatedAt())
                .build();
        publisher.publish(event);
    }

    @Override
    public void rollback(Request request, RequestTask task) {
        log.info("Rollback not required for {}", getHandlerId());
    }
}
