package faang.school.accountservice.service.request;

import faang.school.accountservice.entity.request.Request;
import faang.school.accountservice.entity.request.RequestStatus;
import faang.school.accountservice.repository.request.RequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestService {

    private final RequestRepository requestRepository;

    public void startProcessCreateAccount(List<Request> requests) {
        requests.forEach(request -> Request.builder().requestStatus(RequestStatus.SUCCESS).build());
        requestRepository.saveAll(requests);
    }

    public List<Request> findRequestScheduled() {
       return requestRepository.findScheduledAt();
    }
}
