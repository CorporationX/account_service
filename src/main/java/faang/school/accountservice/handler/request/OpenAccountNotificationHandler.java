package faang.school.accountservice.handler.request;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.enums.RequestHandlerType;
import faang.school.accountservice.mapper.OpenAccountEventMapper;
import faang.school.accountservice.publisher.OpenAccountEventPublisher;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OpenAccountNotificationHandler implements RequestTaskHandler<AccountDto> {
    private final OpenAccountEventPublisher publisher;
    private final OpenAccountEventMapper mapper;

    @Override
    public void execute(AccountDto accountDto) {
        publisher.publish(mapper.toEntity(accountDto));
    }

    @Override
    public RequestHandlerType getHandlerId() {
        return RequestHandlerType.OPEN_ACCOUNT_NOTIFICATION_HANDLER;
    }
}
