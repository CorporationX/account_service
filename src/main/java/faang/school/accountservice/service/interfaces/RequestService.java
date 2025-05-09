package faang.school.accountservice.service.interfaces;

import faang.school.accountservice.entity.Request;
import faang.school.accountservice.enums.RequestType;

import java.util.UUID;
import java.util.function.Function;

public interface RequestService {

    <T> T processRequest(
            UUID idempotencyToken,
            Long userId,
            RequestType type,
            String lockKey,
            Object input,
            Function<Request, T> handler
    );
}
