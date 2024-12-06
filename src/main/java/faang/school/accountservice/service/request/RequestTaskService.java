package faang.school.accountservice.service.request;

import faang.school.accountservice.dto.account.RequestDto;
import faang.school.accountservice.entity.request.RequestTask;
import faang.school.accountservice.repository.request.RequestTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RequestTaskService {

    private final RequestTaskRepository requestTaskRepository;

    public void saveRequestTask(RequestDto requestDto) {

        RequestTask task = RequestTask.builder()
                .handler(requestDto.getRequestStatus().name())
                .createdAt(LocalDateTime.now())
                .build();

        requestTaskRepository.save(task);
    }
}
