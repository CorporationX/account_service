package faang.school.accountservice.handler.request;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.enums.RequestHandlerType;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.repository.BalanceJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BalanceRecordHandler implements RequestTaskHandler<AccountDto> {
    private final BalanceMapper balanceMapper;
    private final BalanceJpaRepository balanceRepository;
    private final AccountMapper accountMapper;

    @Override
    public void execute(AccountDto accountDto) {
        balanceRepository.save(balanceMapper.accountToEntity(accountMapper.toEntity(accountDto)));
    }

    @Override
    public RequestHandlerType getHandlerId() {
        return RequestHandlerType.BALANCE_RECORD_HANDLER;
    }
}
