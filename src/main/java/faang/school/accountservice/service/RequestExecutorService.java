package faang.school.accountservice.service;

import jakarta.transaction.Transactional;

import java.util.UUID;

public interface RequestExecutorService  {
    @Transactional
    void executeRequest(UUID requestId);
}
