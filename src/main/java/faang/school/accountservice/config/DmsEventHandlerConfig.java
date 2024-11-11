package faang.school.accountservice.config;

import faang.school.accountservice.enums.DmsTypeOperation;
import faang.school.accountservice.handler.dms.DmsEventHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class DmsEventHandlerConfig {
    private final List<DmsEventHandler> handlers;

    @Bean
    public Map<DmsTypeOperation, DmsEventHandler> dmsEventHandlers() {
        Map<DmsTypeOperation, DmsEventHandler> eventHandlers = new HashMap<>();
        handlers.forEach(handler -> eventHandlers.put(handler.getTypeOperation(), handler));
        return eventHandlers;
    }
}
