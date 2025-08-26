package faang.school.accountservice.service;

import faang.school.accountservice.entity.account.Request;
import faang.school.accountservice.repository.RequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class RequestScheduler {
    @Autowired
    private RequestRepository requestRepository;


    @Autowired
    private MailService mailService;

    @Async
    public void scheduleNotification(Long requestId) {
        // Получаем статус запроса
        Request request = requestRepository.findById(requestId).orElseThrow();

        // Отправляем уведомление
        mailService.sendNotification("xxxgendelfxxx@mail.ru", request.getStatus());
    }
}
