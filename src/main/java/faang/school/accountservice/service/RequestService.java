package faang.school.accountservice.service;

import faang.school.accountservice.entity.account.Request;
import faang.school.accountservice.enums.RequestStatus;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface RequestService {
    Request createRequest(Request request);

    Optional<Request> findById(UUID idpToken);

    void updateStatus(UUID idpToken, RequestStatus status);

    void updateContext(UUID idpToken, Map<String, Object> context);
}
