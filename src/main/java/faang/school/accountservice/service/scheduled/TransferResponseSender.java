package faang.school.accountservice.service.scheduled;

import faang.school.accountservice.config.kafka.KafkaTopicsProperties;
import faang.school.accountservice.dto.transfer_request.TransferResponse;
import faang.school.accountservice.entity.TransferRequest;
import faang.school.accountservice.enums.transfer_request.TransferStatus;
import faang.school.accountservice.repository.TransferRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferResponseSender {
    @Value("${scheduled.sendPaymentsToKafka.batchSize}")
    private int batchSize;

    private final TransferRepository transferRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaTopicsProperties topicsProperties;

    private final Map<TransferStatus, String> statusToTopicMap = new EnumMap<>(TransferStatus.class);

    @PostConstruct
    private void initStatusToTopicMap() {
        statusToTopicMap.put(TransferStatus.AUTHORIZED, topicsProperties.getTransferResponse());
        statusToTopicMap.put(TransferStatus.CANCELLED, topicsProperties.getTransferCancelResponse());
        statusToTopicMap.put(TransferStatus.CLEARED, topicsProperties.getTransferClearingResponse());
        statusToTopicMap.put(TransferStatus.ERROR, topicsProperties.getDlqTransfer());
        log.debug("Kafka topic mapping initialized: {}", statusToTopicMap);
    }

    @Transactional
    @Scheduled(cron = "${scheduled.sendPaymentsToKafka.cron}")
    public void retryUnsentPayments() {
        log.info("Start sending transfer messages to Kafka");
        List<TransferRequest> transfers = transferRepository.findTopUnprocessed(PageRequest.of(0, batchSize));

        if (transfers.isEmpty()) {
            return;
        }

        List<CompletableFuture<SendResult<String, Object>>> futures = new ArrayList<>();

        transfers.forEach(transferRequest -> {
            try {
                TransferStatus status = transferRequest.getStatus();

                String topic = statusToTopicMap.getOrDefault(status, topicsProperties.getDlqTransfer());
                TransferResponse response = new TransferResponse(transferRequest.getId(), status);

                CompletableFuture<SendResult<String, Object>> sent = kafkaTemplate.send(topic, transferRequest.getId().toString(), response);
                sent.thenAccept(res -> markAsSent(transferRequest));
                futures.add(sent);
            } catch (Exception e) {
                log.error("Failed to send TransferRequest id {} to Kafka", transferRequest.getId(), e);
            }
        });

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    private void markAsSent(TransferRequest transferRequest) {
        transferRequest.setKafkaPublished(true);
        transferRepository.save(transferRequest);
        log.debug("TransferRequest {} sent to kafka", transferRequest.getId());
    }
}
