package faang.school.accountservice.listener;

import faang.school.accountservice.dto.CreateRequestDto;
import faang.school.accountservice.service.RequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventListener {

    private final RequestService requestService;

    @KafkaListener(topics = "${kafka.topics.cons-request}", groupId = "${kafka.group}")
    public void listener(@Valid CreateRequestDto createRequestDto){
        requestService.createRequest(createRequestDto);
    }
}
