package faang.school.accountservice.service.request_task;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.entity.Request;
import faang.school.accountservice.entity.RequestTask;
import faang.school.accountservice.enums.RequestType;
import faang.school.accountservice.repository.RequestRepository;
import faang.school.accountservice.repository.RequestTaskRepository;
import faang.school.accountservice.service.request_task.handler.RequestTaskHandler;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestExecutorService {

    private final List<RequestTaskHandler> requestTaskHandlers;
    private final RequestRepository requestRepository;
    private final RequestTaskRepository requestTaskRepository;
    private final ObjectMapper objectMapper;

    public void executeRequest(long requestId) {
        Request request = requestRepository.findById(requestId).orElseThrow(
                () -> new EntityNotFoundException("Request with id " + requestId + " not found"));

        List<Long> requestTasksIds;
        try {
            requestTasksIds = objectMapper.readValue(request.getContext(), new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing Json", e);
        }
        // do I need it here
        List<RequestTask> requestTasks = requestTaskRepository.findAllById(requestTasksIds);

        List<Long> handlersIds = getHandlersIdsByRequestType(request.getRequestType());
        requestTaskHandlers.stream()
                .filter(handler -> handlersIds.stream()
                        .anyMatch(handlerId -> handlerId.equals(handler.getHandlerId())))
                .peek(RequestTaskHandler::execute);
    }

    private List<Long> getHandlersIdsByRequestType(RequestType requestType) {
        return switch (requestType) {
            case CREATE_ACCOUNT -> new ArrayList<>(List.of(1L, 2L, 3L, 4L, 5L));

        };
    }
}