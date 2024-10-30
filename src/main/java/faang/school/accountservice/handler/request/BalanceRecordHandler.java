package faang.school.accountservice.handler.request;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.enums.RequestHandlerType;
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
        balanceRepository.save(balanceMapper.toEntity(balanceDto));
    }

    @Override
    public RequestHandlerType getHandlerId() {
        return RequestHandlerType.BALANCE_RECORD_HANDLER;
    }
}
