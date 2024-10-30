package faang.school.accountservice.service;

public interface RequestExecutorService {
    @Transactional
    void executeRequest(Long requestId);
}
