package faang.school.accountservice.listener;

import faang.school.accountservice.entity.Request;
import faang.school.accountservice.enums.RequestStatus;
import faang.school.accountservice.repository.RequestRepository;
import faang.school.accountservice.service.RequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OpenCloseRequest {

    private final RequestRepository requestRepository;
    private final RequestService requestService;

    @Async
    @Scheduled(cron = "${cron.expression}")
    public void openRequests() {
        List<Request> requests = requestRepository.findAll();

        for (Request request : requests) {
            if (request.getStatus().equals(RequestStatus.READY_FOR_EXECUTION)) {
                requestService.openRequest(request);
                log.info("Request from user id = {} is open", request.getUserId());
            }
        }
    }

    @Async
    @Scheduled(cron = "${cron.expression}")
    public void closeRequests() {
        List<Request> requests = requestRepository.findAll();

        for (Request request : requests) {
            if (request.getStatus().equals(RequestStatus.PROCESSED)) {
                requestService.closeRequest(request);
                log.info("Request from user id = {} is close", request.getUserId());
            }
        }
    }
}
