package faang.school.accountservice.handler.request;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.BalanceJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BalanceRecordHandler implements RequestTaskHandler {
    private final AccountMapper accountMapper;
    private final BalanceJpaRepository balanceRepository;

    @Override
    public void execute(AccountDto accountDto) {
        balanceRepository.save(accountMapper.toEntity(accountDto).getBalance());
    }

    @Override
    public Long getHandlerId() {
        return 3L;
    }
}
