package faang.school.accountservice.kafka;

import faang.school.accountservice.dto.CreateRequestDto;
import faang.school.accountservice.dto.dms.PendingRequestDto;
import faang.school.accountservice.dto.dms.ResponseClearingDto;
import faang.school.accountservice.service.PendingService;
import faang.school.accountservice.service.RequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventListener {

    private final RequestService requestService;
    private final PendingService pendingService;

    @KafkaListener(topics = "${kafka.topics.cons-request}",
            groupId = "${kafka.group}",
            containerFactory = "createRequestKafkaListenerContainerFactory")
    public void listener(@Valid CreateRequestDto createRequestDto) {
        requestService.createRequest(createRequestDto);
    }

    @KafkaListener(
            topicPartitions = @TopicPartition(
                    topic = "${kafka.topics.pending-topic}",
                    partitions = {"${kafka.partitions.partition-pending-request}"}
            ),
            groupId = "${kafka.group}",
            containerFactory = "pendingRequestKafkaListenerContainerFactory")
    public void listener(PendingRequestDto requestDto) {
        log.info("___________________________Request with {} ___________________________", requestDto.getOperationId());
        pendingService.authorizationPending(requestDto);
    }

    @KafkaListener(
            topicPartitions = @TopicPartition(
                    topic = "${kafka.topics.pending-topic}",
                    partitions = "${kafka.partitions.partition-clearing}"
            ),
            groupId = "${kafka.group}",
            containerFactory = "responseClearingConcurrentKafkaListenerContainerFactory"
    )
    public void listener(ResponseClearingDto clearingDto) {
        log.info("____________Clearing operation id = {} ___________________", clearingDto.getOperationId());
        pendingService.clearingPending(clearingDto);
    }
}
