package faang.school.accountservice.service.request;

import faang.school.accountservice.entity.Request;
import faang.school.accountservice.enums.request.RequestType;
import faang.school.accountservice.repository.RequestRepository;
import faang.school.accountservice.request_executor.RequestProcessExecutor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduledRequestExecutorService {

    private final RequestExecutorService requestExecutorService;
    private final RequestRepository requestRepository;
    private final List<RequestProcessExecutor> processExecutors;

    public void execute() {
        List<Request> requests = requestRepository.findAllAwaitingRequests();
        Map<RequestType, List<Request>> sortedRequests = requests.stream()
                .collect(Collectors.groupingBy(Request::getRequestType));

        for (Map.Entry<RequestType, List<Request>> requestMap : sortedRequests.entrySet()) {
            processExecutors.forEach(processExecutor -> {
                if (requestMap.getKey().equals(processExecutor.getRequestType())) {
                    requestMap.getValue().forEach(request -> processExecutor.getThreadPoolExecutor().
                            execute(() -> requestExecutorService.executeRequest(request)));
                }
            });
        }
    }
}
