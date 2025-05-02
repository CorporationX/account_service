package faang.school.accountservice.service.request.handler;

import faang.school.accountservice.entity.Request;
import faang.school.accountservice.mapper.RequestMapper;
import faang.school.accountservice.service.kafka.publisher.KafkaPublisher;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class KafkaRequestHandler {
    protected final KafkaPublisher kafkaPublisher;
    protected final RequestMapper requestMapper;

    protected KafkaRequestHandler(KafkaPublisher kafkaPublisher, RequestMapper requestMapper) {
        this.kafkaPublisher = kafkaPublisher;
        this.requestMapper = requestMapper;
    }

    protected void sendToKafka(Request request, String topic) {
        kafkaPublisher.sendInTransaction(requestMapper.requestToRequestResponseDto(request), topic);
        log.info("Request sent {} to topic {}", request, topic);
    }
}
