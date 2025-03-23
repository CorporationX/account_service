package faang.school.accountservice.service.request.handler;

import faang.school.accountservice.enums.RequestType;
import faang.school.accountservice.model.Request;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TransferRequestHandler implements RequestHandler {

    @Override
    public RequestType getType() {
        return RequestType.TRANSFER;
    }

    @Override
    public void handle(Request request) {
        log.info("Funds transfer");
    }
}
