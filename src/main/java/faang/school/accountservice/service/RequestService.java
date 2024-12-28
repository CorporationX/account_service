package faang.school.accountservice.service;

import faang.school.accountservice.entity.Request;
import faang.school.accountservice.entity.RequestTask;
import faang.school.accountservice.enums.request.RequestStatus;
import faang.school.accountservice.enums.request.RequestType;
import faang.school.accountservice.enums.request_task.RequestTaskStatus;
import faang.school.accountservice.enums.request_task.RequestTaskType;
import faang.school.accountservice.repository.RequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RequestService {

    private final RequestRepository requestRepository;

    //check: do request tasks save together with request
    public void updateRequest(Request request) {
        requestRepository.save(request);
    }

    // add tasks by request type
    // think about generating UUID
    public Request createRequest(RequestType requestType, LocalDateTime scheduledAt) {
        UUID requestId = UUID.fromString(requestType + String.valueOf(LocalDateTime.now()));

        Request request = Request.builder()
                .idempotentToken(requestId)
                .requestType(requestType)
                .requestStatus(RequestStatus.AWAITING)
                .scheduledAt(scheduledAt)
                .build();

        List<RequestTask> requestTasks = createRequestTasksForRequest(request);
        request.setRequestTasks(requestTasks);
        return requestRepository.save(request);
    }

    private List<RequestTask> createRequestTasksForRequest(Request request) {
        List<RequestTask> requestTasks = new ArrayList<>();
        List<RequestTaskType> requestTasksTypes =
                getRequestTaskTypesByRequestType(request.getRequestType());

        requestTasksTypes.forEach(requestTaskType ->
                requestTasks.add(RequestTask.builder()
                        .handler(requestTaskType)
                        .status(RequestTaskStatus.AWAITING)
                        .request(request)
                        .build()
                ));
        return requestTasks;
    }

    private List<RequestTaskType> getRequestTaskTypesByRequestType(RequestType requestType) {
        List<RequestTaskType> requestTaskTypes = new ArrayList<>();
        switch (requestType) {
            case CREATE_ACCOUNT -> requestTaskTypes = List.of(RequestTaskType.CHECK_ACCOUNTS_QUANTITY,
                    RequestTaskType.WRITE_INTO_ACCOUNT, RequestTaskType.WRITE_INTO_CASHBACK,
                    RequestTaskType.WRITE_INTO_BALANCE_BALANCE_AUDIT, RequestTaskType.SENT_NOTIFICATION);
        }
        return requestTaskTypes;
    }
}
