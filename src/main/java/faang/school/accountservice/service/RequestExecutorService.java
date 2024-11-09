package faang.school.accountservice.service;

import faang.school.accountservice.enums.RequestStatus;

import java.util.UUID;

public interface RequestExecutorService {

    void executeRequest(UUID requestId);

    RequestStatus getStatus(UUID requestId);
}
