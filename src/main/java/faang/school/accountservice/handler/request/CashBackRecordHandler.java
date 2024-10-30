package faang.school.accountservice.handler.request;

import faang.school.accountservice.entity.CashBack;
import faang.school.accountservice.enums.RequestHandlerType;
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
    public RequestHandlerType getHandlerId() {
        return RequestHandlerType.CASHBACK_RECORD_HANDLER;
    }
}
