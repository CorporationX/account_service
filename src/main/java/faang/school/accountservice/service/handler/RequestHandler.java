package faang.school.accountservice.service.handler;

import faang.school.accountservice.entity.Request;
import faang.school.accountservice.enums.RequestType;

public interface RequestHandler {

    RequestType getType();

    void handle(Request request);
}
