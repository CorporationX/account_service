package faang.school.accountservice.service.request.handler;

import faang.school.accountservice.enums.RequestType;
import faang.school.accountservice.model.Request;

public interface RequestHandler {
    RequestType getType();

    void handle(Request request);
}
