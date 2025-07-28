package faang.school.accountservice.kafka;

import faang.school.accountservice.dto.dms.RequestOpenDto;
import faang.school.accountservice.entity.Request;
import faang.school.accountservice.enums.RequestStatus;
import faang.school.accountservice.repository.RequestRepository;
import faang.school.accountservice.service.RequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OpenCloseRequest {

    @Value("${kafka.partitions.partition-pending-response}")
    private int partitionResponse;

    @Value("${kafka.topics.pending-topic}")
    private String pendingTopic;

    private final RequestRepository requestRepository;
    private final RequestService requestService;
    private final KafkaProducerService kafkaProducerService;

    @Async
    @Scheduled(cron = "${cron.expression}")
    public void openRequests() {
        List<Request> requests = requestRepository.findAllByStatus(RequestStatus.IN_PROGRESS);

        requests.forEach(request -> {
            try{
                requestService.openRequest(request);

                sendRequest(request, null);
                log.info("Request opened, id = {}", request.getId());
            }catch(Exception e){
                sendRequest(request, e.getMessage());
                log.error("Failed to open request {} {}", request.getId(), e.getMessage());
            }
        });
    }

    @Async
    @Scheduled(cron = "${cron.expression}")
    public void closeRequests() {
        List<Request> requests = requestRepository.findAllByStatus(RequestStatus.PROCESSED);

        requests.forEach(request -> {
            try{
                requestService.closeRequest(request);
                log.info("Request close, id = {}", request.getId());
            } catch (Exception e){
                log.error("Failed to close request {} {}", request.getId(), e.getMessage());
            }
        });
    }

    private void sendRequest(Request request, String reason){
        RequestOpenDto clearingDto =
                new RequestOpenDto(request.getStatus(), request.getRequestInputData().get("operationId"), reason);

        kafkaProducerService.sendMessage(clearingDto, pendingTopic, partitionResponse);
    }
}
