package faang.school.accountservice.service;

import jakarta.transaction.Transactional;

public interface RequestExecutorService {
    @Transactional
    void executeRequest(Long requestId);
}
