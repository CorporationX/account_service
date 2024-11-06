package faang.school.accountservice.handler;

import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.mapper.OpenAccountEventMapper;
import faang.school.accountservice.publisher.OpenAccountEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationHandler implements RequestTaskHandler<AccountDto> {
    private final OpenAccountEventPublisher publisher;
    private final OpenAccountEventMapper mapper;

    @Override
    public void execute(AccountDto accountDto) {
        publisher.publish(mapper.toEntity(accountDto));
    }

    @Override
    public Long getHandlerId() {
        return 5L;
    }
}
