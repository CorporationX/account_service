package faang.school.accountservice.service.interfaces;

import faang.school.accountservice.entity.Request;
import faang.school.accountservice.enums.RequestType;

import java.util.*;
import java.util.function.Function;

public interface RequestService {

    <T> T processRequest(
            String idempotencyToken,
            Long userId,
            RequestType type,
            String lockKey,
            Map<String, Object> input,
            Function<Request, T> handler
    );
}
