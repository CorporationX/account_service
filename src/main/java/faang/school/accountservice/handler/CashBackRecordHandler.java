package faang.school.accountservice.handler;

import faang.school.accountservice.entity.CashBack;
import faang.school.accountservice.repository.CashBackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CashBackRecordHandler implements RequestTaskHandler<CashBack> {
    private final CashBackRepository cashBackRepository;

    @Override
    public void execute(CashBack param) {
        cashBackRepository.save(param);
    }

    @Override
    public Long getHandlerId() {
        return 6L;
    }
}

