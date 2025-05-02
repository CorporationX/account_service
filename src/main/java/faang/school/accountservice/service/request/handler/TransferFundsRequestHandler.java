package faang.school.accountservice.service.request.handler;

import faang.school.accountservice.entity.Request;
import faang.school.accountservice.enums.request.RequestType;
import faang.school.accountservice.mapper.RequestMapper;
import faang.school.accountservice.service.kafka.publisher.KafkaPublisher;
import org.springframework.beans.factory.annotation.Value;

public class TransferFundsRequestHandler extends KafkaRequestHandler implements RequestHandler {

    protected TransferFundsRequestHandler(KafkaPublisher kafkaPublisher, RequestMapper requestMapper) {
        super(kafkaPublisher, requestMapper);
    }

    @Value("${spring.kafka.producer.topics.request.transfer-funds-topic}")
    private String topic;

    @Override
    public RequestType getRequestType() {
        return RequestType.TRANSFER_FUNDS;
    }

    @Override
    public void handle(Request request) {
        sendToKafka(request, topic);
    }
}
