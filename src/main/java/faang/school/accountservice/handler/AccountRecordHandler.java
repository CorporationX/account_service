package faang.school.accountservice.handler;

import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.mapper.RequestMapper;
import faang.school.accountservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountRecordHandler implements RequestTaskHandler<AccountDto> {
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    //private final RequestJpaRepository requestRepository;
    //private final RequestMapper requestMapper;

    @Override
    public void execute(AccountDto accountDto) {
        accountRepository.save(accountMapper.toEntity(accountDto));
        //requestRepository.save(requestMapper.accountToRequest(accountMapper.toEntity(accountDto)));
    }

    @Override
    public Long getHandlerId() {
        return 2L;
    }
}

