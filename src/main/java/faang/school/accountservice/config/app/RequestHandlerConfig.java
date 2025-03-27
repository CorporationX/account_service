package faang.school.accountservice.config.app;

import faang.school.accountservice.service.request.handler.RequestHandler;
import faang.school.accountservice.service.request.handler.RequestHandlerFactory;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class RequestHandlerConfig {
    private final RequestHandlerFactory requestHandlerFactory;
    private final List<RequestHandler> handlers;

    @PostConstruct
    public void registerHandlers() {
        handlers.forEach(handler -> {
            requestHandlerFactory.addHandler(handler.getType(), handler);
        });
    }
}
