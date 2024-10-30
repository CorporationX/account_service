package faang.school.accountservice.handler.request;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.enums.RequestHandlerType;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.mapper.RequestMapper;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.RequestJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountRecordHandler implements RequestTaskHandler<AccountDto> {
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final RequestJpaRepository requestRepository;
    private final RequestMapper requestMapper;

    @Override
    public void execute(AccountDto accountDto) {
        accountRepository.save(accountMapper.toEntity(accountDto));
        requestRepository.save(requestMapper.accountToRequest(accountMapper.toEntity(accountDto)));
    }

    @Override
    public RequestHandlerType getHandlerId() {
        return RequestHandlerType.ACCOUNT_RECORD_HANDLER;
    }
}
