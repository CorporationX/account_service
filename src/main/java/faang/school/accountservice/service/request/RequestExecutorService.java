package faang.school.accountservice.service.request;

import java.util.UUID;

public interface RequestExecutorService {
    void execute(UUID requestId);
}
