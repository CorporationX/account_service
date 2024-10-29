package faang.school.accountservice.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.dms.DmsEventDto;
import faang.school.accountservice.handler.dms.DmsEventHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component("dmsEventListener")
@RequiredArgsConstructor
@Slf4j
public class DmsEventListener implements MessageListener {
    private final List<DmsEventHandler> handlers;
    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            DmsEventDto dmsEventDto = objectMapper.readValue(message.getBody(), DmsEventDto.class);
            log.info("Event has been received: {}", dmsEventDto);

            DmsEventHandler eventHandler = null;
            for (DmsEventHandler handler : handlers) {
                if (handler.getTypeOperation() == dmsEventDto.getTypeOperation()) {
                    eventHandler = handler;
                    break;
                }
            }
            if (eventHandler == null) {
                String messageError = "Handler for the event(%s) could not be determined".formatted(dmsEventDto);
                log.error(messageError);
                throw new RuntimeException(messageError);
            }
            eventHandler.handle(dmsEventDto);


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
