package faang.school.accountservice.handler;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.repository.BalanceJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BalanceRecordHandler implements RequestTaskHandler<BalanceDto> {
    private final BalanceMapper balanceMapper;
    private final BalanceJpaRepository balanceRepository;

    @Override
    public void execute(BalanceDto balanceDto) {
        Balance balance = balanceMapper.toEntity(balanceDto);
        balance.setVersion(1);
        balanceRepository.save(balance);
    }

    @Override
    public Long getHandlerId() {
        return 3L;
    }
}
