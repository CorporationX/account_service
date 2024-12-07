package faang.school.accountservice.listener.kafka.listeners.achievement;

import faang.school.accountservice.listener.kafka.event.AchievementAcceptedEvent;
import faang.school.accountservice.service.savings_account.SavingsAccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaAchievementAcceptedConsumer {
    private final SavingsAccountService savingsAccountService;

//    @KafkaListener(
//            topics = "${spring.kafka.topic.achievement-accepted-topic}",
//            groupId = "${spring.kafka.consumer.group-id}",
//            containerFactory = "kafkaListenerContainerFactory")
//    public void handle(AchievementAcceptedEvent achievementAcceptedEvent, Acknowledgment acknowledgment) {
//        try {
//            long userId = achievementAcceptedEvent.getUserId();
//            long points = achievementAcceptedEvent.getPoints();
//            savingsAccountService.updateBonusAfterAchievementAccepted(userId, points);
//            acknowledgment.acknowledge();
//        } catch (Exception e) {
//            log.error("Post View with id {} is not added ???", achievementAcceptedEvent.getTitle());
//            throw e;
//        }
//    }
}
