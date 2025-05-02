package faang.school.accountservice.service.request.handler;

import faang.school.accountservice.entity.Request;
import faang.school.accountservice.enums.request.RequestType;

public interface RequestHandler {
    RequestType getRequestType();

    void handle(Request request);
}
