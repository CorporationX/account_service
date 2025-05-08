package faang.school.accountservice.service.request.handler;

import faang.school.accountservice.entity.Request;
import faang.school.accountservice.enums.request.RequestType;
import faang.school.accountservice.mapper.request.RequestMapper;
import faang.school.accountservice.service.kafka.publisher.KafkaPublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CreateAccountRequestHandler extends KafkaRequestHandler implements RequestHandler {

    protected CreateAccountRequestHandler(KafkaPublisher kafkaPublisher, RequestMapper requestMapper) {
        super(kafkaPublisher, requestMapper);
    }

    @Value("${spring.kafka.producer.topics.request.create-account-topic}")
    private String topic;

    @Override
    public RequestType getRequestType() {
        return RequestType.CREATE_ACCOUNT;
    }

    @Override
    public void handle(Request request) {
        sendToKafka(request, topic);
    }
}
