package faang.school.accountservice.service.request;

import faang.school.accountservice.entity.request.Request;
import faang.school.accountservice.repository.request.RequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestSchedulerService {

    private final RequestRepository requestRepository;

    /**
     * В процессе разработки.
     **/
//    @Scheduled(fixedRate = 3000)
    public void scheduled() {
        List<Request> scheduledAt = requestRepository.findScheduledAt();
    }
}

