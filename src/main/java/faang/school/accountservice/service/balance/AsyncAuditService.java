package faang.school.accountservice.service.balance;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AsyncAuditService {

    @Async("captureBalanceExecutor")
    public void processAudit() {
    }
}
