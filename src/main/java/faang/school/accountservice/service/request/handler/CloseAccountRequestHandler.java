package faang.school.accountservice.service.request.handler;

import faang.school.accountservice.entity.Request;
import faang.school.accountservice.enums.request.RequestType;
import faang.school.accountservice.mapper.RequestMapper;
import faang.school.accountservice.service.kafka.publisher.KafkaPublisher;
import org.springframework.beans.factory.annotation.Value;

public class CloseAccountRequestHandler extends KafkaRequestHandler implements RequestHandler {

    protected CloseAccountRequestHandler(KafkaPublisher kafkaPublisher, RequestMapper requestMapper) {
        super(kafkaPublisher, requestMapper);
    }

    @Value("${spring.kafka.producer.topics.request.close-account-topic}")
    private String topic;

    @Override
    public RequestType getRequestType() {
        return RequestType.CLOSE_ACCOUNT;
    }

    @Override
    public void handle(Request request) {
        sendToKafka(request, topic);
    }
}
