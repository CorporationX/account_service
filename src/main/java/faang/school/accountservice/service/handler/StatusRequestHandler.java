package faang.school.accountservice.service.handler;

import faang.school.accountservice.entity.Request;
import faang.school.accountservice.enums.RequestType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class StatusRequestHandler implements RequestHandler {

    @Override
    public RequestType getType() {
        return RequestType.TRANSFER;
    }

    @Override
    public void handle(Request request) {
        log.info("Transfer money...");
    }
}
