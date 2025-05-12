package faang.school.accountservice.service.handler;

import faang.school.accountservice.enums.RequestType;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class RequestHandlerFactory {

    private final Map<String, RequestHandler> handlers = new HashMap<>();

    public void addHandler(RequestType requestType, RequestHandler handler) {
        handlers.put(requestType.name(), handler);
    }

    public RequestHandler getHandler(RequestType requestType) {
        return handlers.get(requestType.name());
    }
}
